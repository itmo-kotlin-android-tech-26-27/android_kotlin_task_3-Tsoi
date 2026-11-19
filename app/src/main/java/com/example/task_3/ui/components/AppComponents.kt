package com.example.task_3.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.example.task_3.R
import com.example.task_3.domain.AttendanceStats
import com.example.task_3.ui.ScreenStatus
import com.example.task_3.ui.theme.AppDimens

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    showBrand: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.statusBarsPadding().padding(
            horizontal = AppDimens.screenPadding,
            vertical = AppDimens.spaceNormal,
        ),
        verticalArrangement = Arrangement.spacedBy(AppDimens.spaceSmall),
    ) {
        if (showBrand) {
            Text(
                stringResource(R.string.organization_name),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.spaceMedium),
        ) {
            if (onBack != null) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(AppDimens.navigationSize),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            Text(
                title,
                style = if (showBrand) MaterialTheme.typography.headlineMedium
                else MaterialTheme.typography.titleLarge,
            )
        }
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AppButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = AppDimens.buttonHeight),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(label)
    }
}

@Composable
fun SelectableCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().semantics { selected = isSelected },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            AppDimens.borderWidth,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        content()
    }
}

@Composable
fun Chevron() {
    Icon(
        painterResource(R.drawable.ic_chevron_right),
        contentDescription = null,
        modifier = Modifier.size(AppDimens.iconSize),
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun MetricTile(value: Int, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(AppDimens.borderWidth, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(AppDimens.spaceTiny),
        ) {
            Text(value.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun attendanceText(percent: Int?): String = percent?.let {
    stringResource(R.string.percent_format, it)
} ?: stringResource(R.string.no_attendance)

@Composable
fun AttendanceCard(
    title: String,
    stats: AttendanceStats,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val foreground = if (highlighted) colors.onPrimary else colors.primary
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (highlighted) colors.primary else colors.surface,
        border = if (highlighted) null else BorderStroke(AppDimens.borderWidth, colors.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(if (highlighted) AppDimens.spaceNormal else AppDimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.spaceSmall),
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = foreground)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.spaceNormal),
            ) {
                Text(
                    attendanceText(stats.percent),
                    style = if (highlighted) MaterialTheme.typography.displayMedium
                    else MaterialTheme.typography.displayLarge,
                    color = foreground,
                )
                if (highlighted) {
                    Text(stringResource(R.string.four_weeks), style = MaterialTheme.typography.bodySmall, color = foreground)
                }
            }
            if (stats.percent != null) {
                if (!highlighted) LinearProgressIndicator(
                    progress = { stats.progress },
                    modifier = Modifier.fillMaxWidth().heightIn(min = AppDimens.progressHeight)
                        .clearAndSetSemantics { },
                    color = colors.primary,
                    trackColor = colors.primaryContainer,
                )
                Text(
                    stringResource(R.string.visits_format, stats.attendedVisits, stats.possibleVisits),
                    style = MaterialTheme.typography.bodySmall,
                    color = foreground,
                )
            } else {
                Text(stringResource(R.string.no_trainings), color = foreground)
            }
        }
    }
}

@Composable
fun StateContent(
    status: ScreenStatus,
    @StringRes notFoundTitle: Int,
    onRetry: () -> Unit,
    onHome: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when (status) {
        ScreenStatus.LOADING -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.spaceNormal),
            ) {
                CircularProgressIndicator()
                Text(stringResource(R.string.loading))
            }
        }
        ScreenStatus.ERROR -> Box(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            StateMessage(
                title = stringResource(R.string.load_error_title),
                message = stringResource(R.string.load_error_message),
                buttonLabel = stringResource(R.string.retry),
                onClick = onRetry,
            )
        }
        ScreenStatus.NOT_FOUND -> Box(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            StateMessage(
                title = stringResource(notFoundTitle),
                message = stringResource(R.string.not_found_message),
                buttonLabel = stringResource(R.string.to_sections),
                onClick = onHome ?: onRetry,
            )
        }
        ScreenStatus.READY -> content()
    }
}

@Composable
fun StateMessage(
    title: String,
    message: String,
    buttonLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AppDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(AppDimens.spaceNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Icon(
                painterResource(R.drawable.ic_users),
                contentDescription = null,
                modifier = Modifier.padding(AppDimens.spaceNormal).size(AppDimens.iconSize),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        AppButton(buttonLabel, onClick)
    }
}
