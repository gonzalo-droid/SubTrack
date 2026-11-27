package com.gondroid.subtrack.platform

/**
 * PLT-02: Firebase and Google Sign-In only exist as Swift packages, so Swift implements these
 * interfaces (iosApp/iosApp/FirebaseBridges.swift) and hands them to [com.gondroid.subtrack.di.initKoinIos].
 * Data crosses as JSON strings to avoid NSNumber/Bool ambiguity; errors as their message.
 */
interface FirestoreNativeBridge {
    /** [completion] gets the document as a JSON object, null JSON when it does not exist, or an error. */
    fun getDocument(path: String, completion: (json: String?, error: String?) -> Unit)

    /** [completion] gets `{"<docId>": {...}, ...}` with every document of the collection, or an error. */
    fun listCollection(path: String, completion: (json: String?, error: String?) -> Unit)

    /** [writesJson] is `[{"op":"set","path":"…","data":{…}}, {"op":"delete","path":"…"}]`, applied as one batch. */
    fun commit(writesJson: String, completion: (error: String?) -> Unit)
}

interface AuthNativeBridge {
    fun currentUserId(): String?

    /** [completion] gets the signed-in user as JSON (uid, displayName, email, phone, photoUrl, createdAt) or an error. */
    fun signInWithGoogle(idToken: String, accessToken: String, completion: (userJson: String?, error: String?) -> Unit)

    fun reauthenticate(idToken: String, accessToken: String, completion: (error: String?) -> Unit)

    fun signOut(completion: (error: String?) -> Unit)

    fun deleteCurrentUser(completion: (error: String?) -> Unit)
}
