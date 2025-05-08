package com.example.botchat.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Theme Gradients
val BackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(MidnightBlack, AstralBlue, StarlitPurple)
)

val ChatInterfaceGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF1A0033), Color(0xFF4B0082), Color(0xFF8A2BE2)) // Deep purple gradient
)

val ChatBubbleGradientDark = Brush.linearGradient(
    colors = listOf(Color(0xFF483D8B), Color(0xFF00CED1).copy(alpha = 0.5f)) // Slate blue to cyan
)

val InputFieldGradientDark = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2F0047), Color(0xFF6A0DAD).copy(alpha = 0.7f)) // Purple shades
)

val SendButtonGradientDark = Brush.linearGradient(
    colors = listOf(AccentIndigo, ElectricCyan)
)

val InactiveSendButtonGradientDark = Brush.linearGradient(
    colors = listOf(GalacticGray, GalacticGray.copy(alpha = 0.7f))
)

val StopButtonGradientDark = Brush.linearGradient(
    colors = listOf(ErrorRed, ErrorRed.copy(alpha = 0.7f))
)

val TopBarUnderlineDark = Brush.linearGradient(
    colors = listOf(AccentPurple, ElectricCyan)
)

val BottomFadeGradientDark = Brush.verticalGradient(
    colors = listOf(ElectricCyan.copy(alpha = 0.5f), Transparent)
)

val SettingsBackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF2E1B4F), Color(0xFF4B0082), Color(0xFF9400D3)) // Rich purple gradient
)

// Light Theme Gradients
val BackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(CloudWhite, MistGray, SoftGray)
)

val ChatInterfaceGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFE6E6FA), Color(0xFFD8BFD8), Color(0xFFB0E0E6)) // Soft pastel gradient
)

val ChatBubbleGradientLight = Brush.linearGradient(
    colors = listOf(Color(0xFFDDA0DD), AquaTeal.copy(alpha = 0.5f)) // Plum to teal
)

val InputFieldGradientLight = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE0FFFF), Color(0xFFB0C4DE).copy(alpha = 0.7f)) // Light cyan to steel blue
)

val SendButtonGradientLight = Brush.linearGradient(
    colors = listOf(Purple40, PurpleGrey40)
)

val InactiveSendButtonGradientLight = Brush.linearGradient(
    colors = listOf(GreyLight, GreyLight.copy(alpha = 0.7f))
)

val StopButtonGradientLight = Brush.linearGradient(
    colors = listOf(RedAccent, RedLight)
)

val TopBarUnderlineLight = Brush.linearGradient(
    colors = listOf(Purple40.copy(alpha = 0.3f), PurpleGrey40.copy(alpha = 0.3f))
)

val BottomFadeGradientLight = Brush.verticalGradient(
    colors = listOf(AquaTeal, Transparent)
)

val SettingsBackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F8FF), Color(0xFFE6E6FA), Color(0xFFF0FFF0)) // Very light pastel gradient
)

// Common Gradients
val SettingsBorderGradient = Brush.verticalGradient(
    colors = listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
)