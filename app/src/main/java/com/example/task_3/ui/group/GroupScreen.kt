package com.example.task_3.ui.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.task_3.R
import com.example.task_3.ui.ScreenStatus
import com.example.task_3.ui.components.AppButton
import com.example.task_3.ui.components.AppHeader
import com.example.task_3.ui.components.AttendanceCard
import com.example.task_3.ui.components.MetricTile
import com.example.task_3.ui.components.StateContent
import com.example.task_3.ui.theme.AppDimens

@Composable
fun GroupScreen(
    state: GroupUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = stringResource(R.string.group_screen_title),
                subtitle = state.section?.name,
                onBack = onBack,
            )
        },
        bottomBar = {
            if (state.status == ScreenStatus.READY) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(
                        Modifier.navigationBarsPadding().padding(
                            horizontal = AppDimens.screenPadding,
                            vertical = AppDimens.spaceMedium,
                        ),
                    ) {
                        AppButton(stringResource(R.string.home), onHome, Modifier.testTag("home_button"))
                    }
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).testTag("screen_group")) {
            StateContent(state.status, R.string.group_not_found, onRetry, onHome) {
                val group = state.group ?: return@StateContent
                val stats = state.stats ?: return@StateContent
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppDimens.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.spaceNormal),
                ) {
                    item(key = "name") {
                        Text(group.name, style = MaterialTheme.typography.headlineLarge)
                    }
                    item(key = "period") {
                        Text(
                            stringResource(R.string.period_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    item(key = "attendance") {
                        AttendanceCard(stringResource(R.string.group_attendance), stats)
                    }
                    item(key = "metrics") {
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.spaceMedium)) {
                            MetricTile(stats.athleteCount, stringResource(R.string.athletes_label), Modifier.weight(1f))
                            MetricTile(group.trainingCount, stringResource(R.string.trainings_label), Modifier.weight(1f))
                        }
                    }
                    item(key = "formula") {
                        Text(
                            stringResource(R.string.attendance_formula),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    item(key = "explanation") {
                        Text(
                            stringResource(R.string.attendance_explanation),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
