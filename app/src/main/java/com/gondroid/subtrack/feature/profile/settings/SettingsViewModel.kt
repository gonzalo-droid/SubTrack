package com.gondroid.subtrack.feature.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.domain.usecase.subscription.ExportSubscriptionsCsvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportSubscriptionsCsv: ExportSubscriptionsCsvUseCase
) : ViewModel() {

    suspend fun buildExport(): String = exportSubscriptionsCsv()
}

// TODO: [hilt] Remove when @HiltViewModel injection is active
class SettingsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsViewModel(ExportSubscriptionsCsvUseCase(MockSubscriptionRepository())) as T
}
