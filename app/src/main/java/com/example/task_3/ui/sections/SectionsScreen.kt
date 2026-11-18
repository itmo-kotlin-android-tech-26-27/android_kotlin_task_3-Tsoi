package com.example.task_3.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.task_3.R
import com.example.task_3.ui.components.AppHeader
import com.example.task_3.ui.components.Chevron
import com.example.task_3.ui.components.SelectableCard
import com.example.task_3.ui.components.StateContent
import com.example.task_3.ui.components.StateMessage
import com.example.task_3.ui.theme.AppDimens

@Composable
fun SectionsScreen(
    state: SectionsUiState,
    onQueryChange: (String) -> Unit,
    onSectionClick: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            AppHeader(
                title = stringResource(R.string.sections_title),
                subtitle = stringResource(R.string.sections_subtitle),
                showBrand = true,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).testTag("screen_sections")) {
            StateContent(state.status, R.string.section_not_found, onRetry) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().testTag("sections_list"),
                    contentPadding = PaddingValues(AppDimens.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.spaceNormal),
                ) {
                    item(key = "search") {
                        OutlinedTextField(
                            value = state.query,
                            onValueChange = onQueryChange,
                            modifier = Modifier.fillMaxWidth().testTag("sections_search"),
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            trailingIcon = {
                                if (state.query.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                            painterResource(R.drawable.ic_close),
                                            contentDescription = stringResource(R.string.search_clear),
                                        )
                                    }
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                        )
                    }
                    items(state.sections, key = { it.section.id }) { item ->
                        SectionCard(
                            item,
                            isSelected = state.selectedSectionId == item.section.id,
                            onClick = { onSectionClick(item.section.id) },
                        )
                    }
                    if (state.sections.isEmpty()) {
                        item(key = "empty") {
                            StateMessage(
                                title = stringResource(
                                    if (state.isSourceEmpty) R.string.sections_empty_title
                                    else R.string.search_empty_title,
                                ),
                                message = stringResource(
                                    if (state.isSourceEmpty) R.string.sections_empty_message
                                    else R.string.search_empty_message,
                                ),
                                buttonLabel = stringResource(
                                    if (state.isSourceEmpty) R.string.retry else R.string.search_clear,
                                ),
                                onClick = if (state.isSourceEmpty) onRetry else { { onQueryChange("") } },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(item: SectionItemUiState, isSelected: Boolean, onClick: () -> Unit) {
    SelectableCard(onClick, isSelected, Modifier.testTag("section_${item.section.id}")) {
        Column(
            modifier = Modifier.padding(AppDimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.spaceSmall),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.section.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                Chevron()
            }
            Text(
                item.section.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(
                    R.string.section_meta_format,
                    stringResource(R.string.groups_count_format, item.stats.groupCount),
                    stringResource(R.string.athletes_count_format, item.stats.athleteCount),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
