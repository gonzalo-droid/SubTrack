package com.gondroid.subtrack.domain.repository

interface AppConfigRepository {
    /**
     * Lowest build this platform still supports (UPD-01), or null when it cannot be read
     * (no network, no document): the app then keeps working (UPD-02).
     */
    suspend fun minSupportedVersion(): Int?
}
