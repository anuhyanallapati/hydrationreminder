//AlarmmanagerTheme.kt
package com.example.alarmmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AlarmmanagerTheme(content: @Composable () -> Unit) {
    val darkColorScheme = darkColorScheme(
        primary = Color.Black,
        onPrimary = Color.White
    )

    val lightColorScheme = lightColorScheme(
        primary = Color.Black,
        onPrimary = Color.White
    )

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme else lightColorScheme,
        typography = Typography,
        content = content
    )
}
