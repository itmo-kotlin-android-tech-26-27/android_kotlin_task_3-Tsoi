package com.example.task_3.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.task_3.R
import com.example.task_3.ui.components.AppHeader
import com.example.task_3.ui.components.AttendanceCard
import com.example.task_3.ui.components.Chevron
import com.example.task_3.ui.components.MetricTile
import com.example.task_3.ui.components.SelectableCard
import com.example.task_3.ui.components.StateContent
import com.example.task_3.ui.components.StateMessage
import com.example.task_3.ui.components.attendanceText
import com.example.task_3.ui.theme.AppDimens

@Composable
fun SectionScreen(
    state: SectionUiState,
    onGroupClick: (String) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = state.section?.name ?: stringResource(R.string.sections_title),
                subtitle = state.section?.location,
                onBack = onBack,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            StateContent(state.status, R.string.section_not_found, onRetry, onHome) {
                val stats = state.stats ?: return@StateContent
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppDimens.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.spaceMedium),
                ) {
                    item(key = "attendance") {
                        AttendanceCard(stringResource(R.string.section_attendance), stats, highlighted = true)
                    }
                    item(key = "metrics") {
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.spaceMedium)) {
                            MetricTile(stats.athleteCount, stringResource(R.string.athletes_label), Modifier.weight(1f))
                            MetricTile(stats.groupCount, stringResource(R.string.groups_label), Modifier.weight(1f))
                        }
                    }
                    item(key = "groups_title") {
                        Text(stringResource(R.string.groups_title), style = MaterialTheme.typography.titleMedium)
                    }
                    items(state.groups, key = { it.group.id }) { item ->
                        GroupCard(
                            item,
                            isSelected = state.selectedGroupId == item.group.id,
                            onClick = { onGroupClick(item.group.id) },
                        )
                    }
                    if (state.groups.isEmpty()) {
                        item(key = "empty") {
                            StateMessage(
                                stringResource(R.string.groups_empty_title),
                                stringResource(R.string.groups_empty_message),
                                stringResource(R.string.to_sections),
                                onHome,
                            )
                        }
                    } else {
                        item(key = "hint") {
                            Text(
                                stringResource(R.string.group_select_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCard(item: GroupItemUiState, isSelected: Boolean, onClick: () -> Unit) {
    SelectableCard(onClick, isSelected) {
        Row(
            modifier = Modifier.padding(AppDimens.spaceNormal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.spaceMedium),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimens.spaceTiny),
            ) {
                Text(item.group.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.athletes_count_format, item.group.athleteCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                attendanceText(item.stats.percent),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Chevron()
        }
    }
}
