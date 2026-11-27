package com.gondroid.subtrack.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.usecase.member.GetAllPeopleWithSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMyDebtsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** "Te deben": people in subscriptions I administer. "Debes": admins I pay. */
enum class PeopleTab { OWED_TO_ME, I_OWE }

sealed interface PeopleUiState {
    data object Loading : PeopleUiState
    data class Success(
        val tab: PeopleTab,
        val people: List<PersonSummary>,
        val owedToMeCount: Int,
        val iOweCount: Int,
        val searchQuery: String,
        val selectedPerson: PersonSummary?
    ) : PeopleUiState
}

@OptIn(FlowPreview::class)
class PeopleViewModel(
    getAllPeopleWithSubscriptions: GetAllPeopleWithSubscriptionsUseCase,
    getMyDebts: GetMyDebtsUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedPerson = MutableStateFlow<PersonSummary?>(null)
    private val tab = MutableStateFlow(PeopleTab.OWED_TO_ME)

    private val lists = combine(getAllPeopleWithSubscriptions(), getMyDebts()) { owedToMe, iOwe -> owedToMe to iOwe }

    val uiState: StateFlow<PeopleUiState> = combine(
        lists,
        tab,
        searchQuery.debounce(250),
        selectedPerson
    ) { (owedToMe, iOwe), currentTab, query, selected ->
        val people = if (currentTab == PeopleTab.OWED_TO_ME) owedToMe else iOwe
        val filtered = if (query.isBlank()) people
        else people.filter { it.name.contains(query.trim(), ignoreCase = true) }
        PeopleUiState.Success(
            tab = currentTab,
            people = filtered,
            owedToMeCount = owedToMe.size,
            iOweCount = iOwe.size,
            searchQuery = query,
            selectedPerson = selected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PeopleUiState.Loading
    )

    fun selectTab(newTab: PeopleTab) = tab.update { newTab }

    fun updateSearchQuery(query: String) = searchQuery.update { query }

    fun selectPerson(person: PersonSummary) = selectedPerson.update { person }

    fun clearSelectedPerson() = selectedPerson.update { null }
}
