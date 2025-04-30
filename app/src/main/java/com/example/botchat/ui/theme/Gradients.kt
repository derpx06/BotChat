package com.example.botchat.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Theme Gradients
val BackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(MidnightBlack, AstralBlue, StarlitPurple)
)

val ChatInterfaceGradientDark = Brush.verticalGradient(
    colors = listOf(StarlitPurple, MidnightBlack)
)

val ChatBubbleGradientDark = Brush.linearGradient(
    colors = listOf(AstralBlue, ElectricCyan.copy(alpha = 0.3f))
)

val InputFieldGradientDark = Brush.horizontalGradient(
    colors = listOf(ObsidianGray, AstralBlue.copy(alpha = 0.7f))
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
    colors = listOf(StarlitPurple, MidnightBlack)
)

// Light Theme Gradients
val BackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(CloudWhite, MistGray, SoftGray)
)

val ChatInterfaceGradientLight = Brush.verticalGradient(
    colors = listOf(IndigoLight, PurpleVivid, AquaTeal)
)

val ChatBubbleGradientLight = Brush.linearGradient(
    colors = listOf(MistGray, AquaTeal.copy(alpha = 0.3f))
)

val InputFieldGradientLight = Brush.horizontalGradient(
    colors = listOf(MistGray.copy(alpha = 0.5f), SoftGray.copy(alpha = 0.5f))
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
    colors = listOf(IndigoLight, CloudWhite)
)

// Common Gradients
val SettingsBorderGradient = Brush.verticalGradient(
    colors = listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
)