package com.gondroid.subtrack.domain.model

/**
 * Tokens from Google's account picker. Android only gets the [idToken]; iOS also gets an
 * [accessToken], which Firebase needs there.
 */
data class GoogleCredential(val idToken: String, val accessToken: String? = null)
