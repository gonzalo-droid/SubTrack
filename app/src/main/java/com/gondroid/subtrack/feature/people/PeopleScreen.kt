package com.gondroid.subtrack.feature.people

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.feature.people.components.PersonRow
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PeopleScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PeopleViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(BgApp)) {
        when (val state = uiState) {
            PeopleUiState.Loading -> Unit
            is PeopleUiState.Success -> {
                val tabLabels = mapOf(
                    PeopleTab.OWED_TO_ME to stringResource(R.string.people_tab_owed_to_me),
                    PeopleTab.I_OWE to stringResource(R.string.people_tab_i_owe)
                )
                LazyColumn(
                    contentPadding = PaddingValues(bottom = Spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.people_title),
                            style = SubTrackType.headlineL,
                            color = TextPrimary,
                            modifier = Modifier
                                .padding(horizontal = Spacing.base)
                                .padding(top = Spacing.l)
                                .semantics { heading() }
                        )
                    }
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgApp)
                                .padding(horizontal = Spacing.base, vertical = Spacing.m),
                            verticalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            SegmentedSelector(
                                options = PeopleTab.entries,
                                selectedIndex = state.tab.ordinal,
                                onSelectionChange = { viewModel.selectTab(PeopleTab.entries[it]) },
                                labelProvider = { tabLabels.getValue(it) },
                                countProvider = {
                                    when (it) {
                                        PeopleTab.OWED_TO_ME -> state.owedToMeCount
                                        PeopleTab.I_OWE -> state.iOweCount
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            STTextField(
                                value = state.searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                label = "",
                                placeholder = stringResource(R.string.people_search_hint),
                                leadingIcon = Icons.Outlined.Search,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (state.people.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when {
                                        state.searchQuery.isNotBlank() -> stringResource(R.string.people_empty_search)
                                        state.tab == PeopleTab.I_OWE -> stringResource(R.string.people_empty_i_owe)
                                        else -> stringResource(R.string.people_empty)
                                    },
                                    style = SubTrackType.bodyM,
                                    color = TextTertiary
                                )
                            }
                        }
                    } else {
                        items(state.people, key = { "${it.userId}-${it.phone}-${it.name}" }) { person ->
                            PersonRow(
                                person = person,
                                onClick = { viewModel.selectPerson(person) },
                                debtLabel = stringResource(
                                    if (state.tab == PeopleTab.I_OWE) R.string.people_i_owe_label else R.string.people_owes_label
                                ),
                                modifier = Modifier.padding(horizontal = Spacing.base)
                            )
                        }
                    }
                }

                if (state.selectedPerson != null) {
                    PersonDetailSheet(
                        person = state.selectedPerson,
                        onDismiss = viewModel::clearSelectedPerson
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PeopleScreenPreview() {
    SubTrackTheme { PeopleScreen(onNavigateToDetail = {}) }
}
