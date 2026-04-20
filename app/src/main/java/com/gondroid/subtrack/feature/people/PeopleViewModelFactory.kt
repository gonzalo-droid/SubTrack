package com.gondroid.subtrack.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gondroid.subtrack.data.mock.MockMemberRepository
import com.gondroid.subtrack.domain.usecase.member.GetAllPeopleWithSubscriptionsUseCase

// TODO: [hilt] Remove when @HiltViewModel injection is active (AGP / Hilt plugin fixed)
class PeopleViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PeopleViewModel(
            getAllPeopleWithSubscriptions = GetAllPeopleWithSubscriptionsUseCase(MockMemberRepository())
        ) as T
    }
}
