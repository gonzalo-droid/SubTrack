package com.gondroid.subtrack.domain.usecase.app

import com.gondroid.subtrack.domain.repository.AppConfigRepository

/** True only when the remote minimum is known and above [installedVersion]; unknown never blocks (UPD-02). */
class IsUpdateRequiredUseCase(private val repository: AppConfigRepository) {
    suspend operator fun invoke(installedVersion: Int): Boolean =
        repository.minSupportedVersion()?.let { installedVersion < it } ?: false
}
