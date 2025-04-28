package com.example.botchat.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(DeepSpaceBlack, NebulaBlue, CosmicPurple)
)

val BackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(LightBackground, LightSecondary, LightTertiary)
)

val ChatInterfaceGradientDark = Brush.verticalGradient(
    colors = listOf(CosmicPurple, NebulaBlue, NeonCyan.copy(alpha = 0.2f))
)

val ChatInterfaceGradientLight = Brush.verticalGradient(
    colors = listOf(IndigoLight, PurpleVivid, TealLight)
)

val InputFieldGradientDark = Brush.horizontalGradient(
    colors = listOf(InputDark, NebulaBlue.copy(alpha = 0.7f))
)

val InputFieldGradientLight = Brush.horizontalGradient(
    colors = listOf(LightSecondary.copy(alpha = 0.5f), GreyLight.copy(alpha = 0.5f))
)

val BottomFadeGradientDark = Brush.verticalGradient(
    colors = listOf(NeonCyan.copy(alpha = 0.5f), Transparent)
)

val BottomFadeGradientLight = Brush.verticalGradient(
    colors = listOf(TealLight, Transparent)
)

val TopBarUnderlineDark = Brush.linearGradient(
    colors = listOf(AccentIndigo, AccentIndigoEnd)
)

val TopBarUnderlineLight = Brush.linearGradient(
    colors = listOf(Purple40.copy(alpha = 0.3f), PurpleGrey40.copy(alpha = 0.3f))
)

val SendButtonGradientDark = Brush.linearGradient(
    colors = listOf(AccentIndigo, NeonCyan)
)

val SendButtonGradientLight = Brush.linearGradient(
    colors = listOf(Purple40, PurpleGrey40)
)

val InactiveSendButtonGradientDark = Brush.linearGradient(
    colors = listOf(GalacticGray, GalacticGray.copy(alpha = 0.7f))
)

val InactiveSendButtonGradientLight = Brush.linearGradient(
    colors = listOf(GreyLight, GreyLight)
)

val StopButtonGradientDark = Brush.linearGradient(
    colors = listOf(ErrorRed, ErrorRed.copy(alpha = 0.7f))
)

val StopButtonGradientLight = Brush.linearGradient(
    colors = listOf(RedAccent, RedLight)
)

val SettingsBackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(CosmicPurple, DeepSpaceBlack)
)

val SettingsBackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(IndigoLight, LightBackground)
)

val SettingsBorderGradient = Brush.verticalGradient(
    colors = listOf(NeonCyan.copy(alpha = 0.3f), Transparent)
)