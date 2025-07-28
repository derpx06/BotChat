package com.example.ChatBlaze.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// Theme Composable
@Composable
fun BotChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = ElectricCyan,
            onPrimary = MidnightBlack,
            surface = MidnightBlack,
            onSurface = PureWhite,
            error = ErrorRed,
            onError = PureWhite
        )
    } else {
        lightColorScheme(
            primary = Purple40,
            onPrimary = PureWhite,
            surface = CloudWhite,
            onSurface = Color.Black,
            error = RedAccent,
            onError = PureWhite
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}