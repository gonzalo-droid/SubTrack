package com.gondroid.subtrack.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.usecase.member.GetAllPeopleWithSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface PeopleUiState {
    data object Loading : PeopleUiState
    data class Success(
        val people: List<PersonSummary>,
        val searchQuery: String,
        val selectedPerson: PersonSummary?
    ) : PeopleUiState
}

@OptIn(FlowPreview::class)
@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val getAllPeopleWithSubscriptions: GetAllPeopleWithSubscriptionsUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedPerson = MutableStateFlow<PersonSummary?>(null)

    val uiState: StateFlow<PeopleUiState> = combine(
        getAllPeopleWithSubscriptions(),
        searchQuery.debounce(250),
        selectedPerson
    ) { people, query, selected ->
        val filtered = if (query.isBlank()) people
        else people.filter { it.name.contains(query.trim(), ignoreCase = true) }
        PeopleUiState.Success(
            people = filtered,
            searchQuery = query,
            selectedPerson = selected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PeopleUiState.Loading
    )

    fun updateSearchQuery(query: String) = searchQuery.update { query }

    fun selectPerson(person: PersonSummary) = selectedPerson.update { person }

    fun clearSelectedPerson() = selectedPerson.update { null }
}
