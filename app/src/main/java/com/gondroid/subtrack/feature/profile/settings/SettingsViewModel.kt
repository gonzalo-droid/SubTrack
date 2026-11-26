package com.gondroid.subtrack.feature.profile.settings

import androidx.lifecycle.ViewModel
import com.gondroid.subtrack.domain.usecase.subscription.ExportSubscriptionsCsvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportSubscriptionsCsv: ExportSubscriptionsCsvUseCase
) : ViewModel() {

    suspend fun buildExport(): String = exportSubscriptionsCsv()
}
