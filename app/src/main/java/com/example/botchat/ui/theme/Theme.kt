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
            secondary = AstralBlue,
            tertiary = ElectricCyan,
            background = MidnightBlack,
            surface = StarlitPurple,
            onPrimary = PureWhite,
            onSecondary = GalacticGray,
            onTertiary = PureWhite,
            onBackground = PureWhite,
            onSurface = PureWhite,
            primaryContainer = AccentIndigoEnd,
            secondaryContainer = ObsidianGray,
            error = ErrorRed,
            onError = PureWhite
        )
        else -> lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = AquaTeal,
            background = CloudWhite,
            surface = IndigoLight,
            onPrimary = White,
            onSecondary = SlateBlack.copy(alpha = 0.7f),
            onTertiary = SlateBlack.copy(alpha = 0.7f),
            onBackground = SlateBlack,
            onSurface = SlateBlack,
            primaryContainer = AccentPurple,
            secondaryContainer = MistGray
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