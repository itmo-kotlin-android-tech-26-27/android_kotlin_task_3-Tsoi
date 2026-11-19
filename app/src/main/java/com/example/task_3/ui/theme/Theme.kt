package com.example.task_3.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Sage,
    onPrimaryContainer = Forest,
    secondary = Lime,
    onSecondary = Forest,
    background = Canvas,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    onSurfaceVariant = Muted,
    outline = Outline,
    outlineVariant = Outline,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(AppDimens.radiusSmall),
    medium = RoundedCornerShape(AppDimens.radiusMedium),
    large = RoundedCornerShape(AppDimens.radiusLarge),
    extraLarge = RoundedCornerShape(AppDimens.radiusExtraLarge),
)

@Composable
fun SportsAnalyticsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
