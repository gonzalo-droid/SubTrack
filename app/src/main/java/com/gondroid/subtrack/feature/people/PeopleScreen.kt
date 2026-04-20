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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.feature.people.components.PersonRow

@Composable
fun PeopleScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PeopleViewModel = viewModel(
        factory = PeopleViewModelFactory()
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(BgApp)) {
        when (val state = uiState) {
            PeopleUiState.Loading -> Unit
            is PeopleUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = Spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                    modifier = Modifier.fillMaxSize()
                ) {
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgApp)
                                .padding(horizontal = Spacing.base, vertical = Spacing.m)
                        ) {
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
                                    text = if (state.searchQuery.isBlank())
                                        stringResource(R.string.people_empty)
                                    else
                                        stringResource(R.string.people_empty_search),
                                    style = SubTrackType.bodyM,
                                    color = TextTertiary
                                )
                            }
                        }
                    } else {
                        items(state.people, key = { it.phone }) { person ->
                            PersonRow(
                                person = person,
                                onClick = { viewModel.selectPerson(person) },
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
