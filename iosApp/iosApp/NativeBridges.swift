import Foundation
import UIKit
import ComposeApp
import FirebaseAuth
import FirebaseCore
import FirebaseFirestore
import GoogleSignIn

// PLT-02: the Firebase and Google Sign-In SDKs only exist as Swift packages, so these classes implement the
// Kotlin interfaces in composeApp/src/iosMain/.../platform/NativeBridges.kt. Data crosses as JSON strings.

/// Without GoogleService-Info.plist Firebase is not configured; calling it would crash, so the bridges fail softly.
private var firebaseReady: Bool { FirebaseApp.app() != nil }
private let notConfigured = "Firebase is not configured: GoogleService-Info.plist is missing"

/// Error text for Kotlin; connectivity errors get the "network:" prefix IosFirebase.kt maps to DataError.Network.
private func describe(_ error: Error) -> String {
    let ns = error as NSError
    let offline = ns.domain == NSURLErrorDomain
        || (ns.domain == AuthErrorDomain && ns.code == AuthErrorCode.networkError.rawValue)
        || (ns.domain == FirestoreErrorDomain && ns.code == FirestoreErrorCode.unavailable.rawValue)
    return (offline ? "network:" : "") + ns.localizedDescription
}

private func jsonString(_ object: Any) -> String? {
    guard JSONSerialization.isValidJSONObject(object),
          let data = try? JSONSerialization.data(withJSONObject: object) else { return nil }
    return String(data: data, encoding: .utf8)
}

final class FirestoreBridge: FirestoreNativeBridge {
    private var db: Firestore { Firestore.firestore() }

    func getDocument(path: String, completion: @escaping (String?, String?) -> Void) {
        guard firebaseReady else { return completion(nil, notConfigured) }
        db.document(path).getDocument { snapshot, error in
            if let error { return completion(nil, describe(error)) }
            guard let snapshot, snapshot.exists else { return completion(nil, nil) }
            completion(jsonString(snapshot.data() ?? [:]) ?? "{}", nil)
        }
    }

    func listCollection(path: String, completion: @escaping (String?, String?) -> Void) {
        guard firebaseReady else { return completion(nil, notConfigured) }
        db.collection(path).getDocuments { snapshot, error in
            if let error { return completion(nil, describe(error)) }
            var documents: [String: Any] = [:]
            snapshot?.documents.forEach { documents[$0.documentID] = $0.data() }
            completion(jsonString(documents) ?? "{}", nil)
        }
    }

    func commit(writesJson: String, completion: @escaping (String?) -> Void) {
        guard firebaseReady else { return completion(notConfigured) }
        guard let data = writesJson.data(using: .utf8),
              let writes = (try? JSONSerialization.jsonObject(with: data)) as? [[String: Any]] else {
            return completion("Invalid writes")
        }
        let batch = db.batch()
        for write in writes {
            guard let path = write["path"] as? String else { continue }
            let document = db.document(path)
            if write["op"] as? String == "delete" {
                batch.deleteDocument(document)
            } else if let fields = write["data"] as? [String: Any] {
                batch.setData(fields, forDocument: document)
            }
        }
        batch.commit { error in completion(error.map(describe)) }
    }
}

final class FirebaseAuthBridge: AuthNativeBridge {
    func currentUserId() -> String? { firebaseReady ? Auth.auth().currentUser?.uid : nil }

    func signInWithGoogle(idToken: String, accessToken: String, completion: @escaping (String?, String?) -> Void) {
        guard firebaseReady else { return completion(nil, notConfigured) }
        let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)
        Auth.auth().signIn(with: credential) { result, error in
            if let error { return completion(nil, describe(error)) }
            guard let user = result?.user else { return completion(nil, "Firebase returned no user") }
            var json: [String: Any] = ["uid": user.uid]
            json["displayName"] = user.displayName
            json["email"] = user.email
            json["phone"] = user.phoneNumber
            json["photoUrl"] = user.photoURL?.absoluteString
            if let created = user.metadata.creationDate { json["createdAt"] = Int64(created.timeIntervalSince1970 * 1000) }
            completion(jsonString(json), nil)
        }
    }

    func reauthenticate(idToken: String, accessToken: String, completion: @escaping (String?) -> Void) {
        guard firebaseReady else { return completion(notConfigured) }
        guard let user = Auth.auth().currentUser else { return completion("No signed-in user") }
        let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)
        user.reauthenticate(with: credential) { _, error in completion(error.map(describe)) }
    }

    func signOut(completion: @escaping (String?) -> Void) {
        guard firebaseReady else { return completion(notConfigured) }
        do {
            try Auth.auth().signOut()
            GIDSignIn.sharedInstance.signOut()
            completion(nil)
        } catch {
            completion(describe(error))
        }
    }

    func deleteCurrentUser(completion: @escaping (String?) -> Void) {
        guard firebaseReady else { return completion(notConfigured) }
        guard let user = Auth.auth().currentUser else { return completion("No signed-in user") }
        user.delete { error in completion(error.map(describe)) }
    }
}

final class GoogleSignInBridge: GoogleSignInNativeBridge {
    func signIn(completion: @escaping (String?, String?, String?) -> Void) {
        guard GIDSignIn.sharedInstance.configuration != nil else {
            return completion(nil, nil, "Google Sign-In is not configured: GoogleService-Info.plist is missing")
        }
        guard let presenter = Self.topViewController() else { return completion(nil, nil, "No window to present from") }
        GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
            if let error = error as NSError? {
                // Closing the picker is not an error for the app
                if error.domain == kGIDSignInErrorDomain && error.code == GIDSignInError.canceled.rawValue {
                    return completion(nil, nil, nil)
                }
                return completion(nil, nil, describe(error))
            }
            guard let user = result?.user, let idToken = user.idToken?.tokenString else {
                return completion(nil, nil, "Google returned no token")
            }
            completion(idToken, user.accessToken.tokenString, nil)
        }
    }

    private static func topViewController() -> UIViewController? {
        let window = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
        var top = window?.rootViewController
        while let presented = top?.presentedViewController { top = presented }
        return top
    }
}
