package com.gondroid.subtrack.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gondroid.subtrack.domain.model.GoogleCredential

/**
 * Shows Google's account picker (Credential Manager) and returns the account's ID token.
 * Needs an Activity context, so the UI calls it and hands the token to the ViewModel.
 */
object GoogleIdTokenProvider {

    /** Token returned in fake-auth mode; the auth repository never sends it to Firebase. */
    const val FAKE_TOKEN = "fake-token"

    /** OAuth web client id from google-services.json; the app sets it at startup (the library cannot read the generated resource). */
    var webClientId: String = ""

    /** Null when the user closed the picker; failure for any other problem. */
    suspend fun request(activityContext: Context): Result<GoogleCredential?> {
        if (AuthConfig.useFakeAuth) return Result.success(GoogleCredential(FAKE_TOKEN))
        val option = GetSignInWithGoogleOption.Builder(webClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        return try {
            val credential = CredentialManager.create(activityContext).getCredential(activityContext, request).credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Result.success(GoogleCredential(GoogleIdTokenCredential.createFrom(credential.data).idToken))
            } else {
                Result.failure(IllegalStateException("Unexpected credential type ${credential.type}"))
            }
        } catch (_: GetCredentialCancellationException) {
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
