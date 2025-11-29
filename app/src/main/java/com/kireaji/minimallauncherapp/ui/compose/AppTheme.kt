package com.kireaji.minimallauncherapp.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val DarkBackground = Color(0xFF121212)  // ソフトなダークグレー
    val LightBackground = Color(0xFFF5F5F5) // ソフトなオフホワイト
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            background = AppColors.DarkBackground,
            surface = AppColors.DarkBackground
        )
    } else {
        lightColorScheme(
            background = AppColors.LightBackground,
            surface = AppColors.LightBackground
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
