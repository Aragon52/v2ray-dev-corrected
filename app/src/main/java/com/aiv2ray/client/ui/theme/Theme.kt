package com.aiv2ray.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    background = Steel.Base,
    surface = Steel.Mid,
    primary = Copper.Front,
    onPrimary = Steel.Base,
    secondary = Iron.EngraveLight,
    onBackground = Iron.EngraveLight,
    onSurface = Iron.EngraveLight,
    error = Iron.StatusLed
)

private val LightColors = lightColorScheme(
    background = Steel.High,
    primary = Copper.Front
)

@Composable
fun AiV2RayTheme(
    darkTheme: Boolean = true, // dark is the default brand direction
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme || isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
