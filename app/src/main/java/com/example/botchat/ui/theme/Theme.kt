package com.example.botchat.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun BotChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = AccentIndigo,
            secondary = NebulaBlue,
            tertiary = NeonCyan,
            background = DeepSpaceBlack,
            surface = CosmicPurple,
            onPrimary = StarlightWhite,
            onSecondary = GalacticGray,
            onTertiary = StarlightWhite,
            onBackground = StarlightWhite,
            onSurface = StarlightWhite,
            primaryContainer = AccentIndigoEnd,
            secondaryContainer = InputDark,
            error = ErrorRed,
            onError = StarlightWhite
        )
        else -> lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40,
            background = LightBackground,
            surface = IndigoLight,
            onPrimary = White,
            onSecondary = Black.copy(alpha = 0.7f),
            onTertiary = Black.copy(alpha = 0.7f),
            onBackground = Black,
            onSurface = Black,
            primaryContainer = AccentPurple,
            secondaryContainer = GreyLight
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}