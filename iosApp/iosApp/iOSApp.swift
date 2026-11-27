import SwiftUI
import ComposeApp
import FirebaseCore
import GoogleSignIn

@main
struct iOSApp: App {
    init() {
        // GoogleService-Info.plist is not in git (public repo): without it the app runs, but sign-in and sync fail
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
            if let clientID = FirebaseApp.app()?.options.clientID {
                GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)
            }
        }
        #if DEBUG
        let isDebug = true
        // E2E and manual tests: `-fakeAuth` skips the real Google sign-in, like the Android intent extra
        if ProcessInfo.processInfo.arguments.contains("-\(AuthConfig.shared.FAKE_AUTH_EXTRA)") {
            AuthConfig.shared.useFakeAuth = true
        }
        #else
        let isDebug = false
        #endif
        IosEntryKt.startSubTrack(
            firestore: FirestoreBridge(),
            auth: FirebaseAuthBridge(),
            googleSignIn: GoogleSignInBridge(),
            isDebug: isDebug
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                // Google's sign-in comes back to the app through its URL scheme
                .onOpenURL { url in GIDSignIn.sharedInstance.handle(url) }
        }
    }
}
