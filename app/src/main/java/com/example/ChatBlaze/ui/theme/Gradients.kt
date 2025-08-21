package com.example.ChatBlaze.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- NEW VIBRANT BLUE GRADIENTS ---
val BackgroundGradientVibrantBlue = Brush.verticalGradient(
    colors = listOf(DeepMidnightBlue, RichNavy, SubtleViolet)
)
val ChatBubbleUserGradientVibrant = Brush.linearGradient(
    colors = listOf(RichNavy, SoftBlueGray)
)
val ChatBubbleAIGradientVibrant = Brush.linearGradient(
    colors = listOf(RichNavy, DeepMidnightBlue)
)

// New Soft Pastel Colors
val BlushPink = Color(0xFFFED7E2)
val SkyBlue = Color(0xFFB3E5FC)
val CreamyMint = Color(0xFFD4F4E2)
val VanillaCream = Color(0xFFFFF5E6)
val PowderBlue = Color(0xFFB0C4DE)
val SoftPeach = Color(0xFFF9E4B7)
val LavenderHaze = Color(0xFFE8DAEF)
val MellowYellow = Color(0xFFFFF3B3)

// New Vibrant Neon Colors
val ElectricPink = Color(0xFFFF2E95)
val VividCyan = Color(0xFF00E7FF)
val LimeBurst = Color(0xFFCCFF00)
val FlameOrange = Color(0xFFFF3D00)
val UltraViolet = Color(0xFF7C4DFF)
val NeonCoral = Color(0xFFFF6F61)
val BrightMagenta = Color(0xFFC51162)
val TurboYellow = Color(0xFFF4E04D)

// Dark Theme Gradients (Unchanged)
val BackgroundGradientDark = Brush.verticalGradient(
    colors = listOf(MidnightBlack, AstralBlue, StarlitPurple)
)
val ChatInterfaceGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF1A0033), Color(0xFF4B0082), Color(0xFF8A2BE2))
)
val ChatBubbleGradientDark = Brush.linearGradient(
    colors = listOf(Color(0xFF483D8B), Color(0xFF00CED1).copy(alpha = 0.5f))
)
val InputFieldGradientDark = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2F0047), Color(0xFF6A0DAD).copy(alpha = 0.7f))
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
    colors = listOf(Color(0xFF2E1B4F), Color(0xFF4B0082), Color(0xFF9400D3))
)

// Light Theme Gradients (Unchanged)
val BackgroundGradientLight = Brush.verticalGradient(
    colors = listOf(CloudWhite, MistGray, SoftGray)
)
val ChatInterfaceGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFE6E6FA), Color(0xFFD8BFD8), Color(0xFFB0E0E6))
)
val ChatBubbleGradientLight = Brush.linearGradient(
    colors = listOf(Color(0xFFDDA0DD), AquaTeal.copy(alpha = 0.5f))
)
val InputFieldGradientLight = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE0FFFF), Color(0xFFB0C4DE).copy(alpha = 0.7f))
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
    colors = listOf(Color(0xFFF0F8FF), Color(0xFFE6E6FA), Color(0xFFF0FFF0))
)

// Common Gradients (Unchanged)
val SettingsBorderGradient = Brush.verticalGradient(
    colors = listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
)
val SleekGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF0A0A1F), Color(0xFF1E3A8A), Color(0xFF4B0082))
)
val SleekGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFF5F5F5), Color(0xFFE6E6FA), Color(0xFFB0E0E6))
)
val CardGradientDark = Brush.linearGradient(
    colors = listOf(Color(0xFF1E3A8A).copy(alpha = 0.3f), Color(0xFF4B0082).copy(alpha = 0.3f))
)
val CardGradientLight = Brush.linearGradient(
    colors = listOf(Color(0xFFE6E6FA).copy(alpha = 0.3f), Color(0xFFB0E0E6).copy(alpha = 0.3f))
)

// Cosmic Theme Gradients (Unchanged)
val CosmicGradientDark = Brush.verticalGradient(
    colors = listOf(MidnightBlack, TwilightBlue, CosmicPink)
)
val CosmicGradientLight = Brush.verticalGradient(
    colors = listOf(CloudWhite, BabyBlue, PeachBlush)
)
val CosmicBubbleGradientDark = Brush.linearGradient(
    colors = listOf(CosmicPink, VioletVibe.copy(alpha = 0.7f))
)
val CosmicBubbleGradientLight = Brush.linearGradient(
    colors = listOf(PeachBlush, AquaTeal.copy(alpha = 0.7f))
)
val CosmicInputGradientDark = Brush.linearGradient(
    colors = listOf(TwilightBlue, ElectricCyan.copy(alpha = 0.7f))
)
val CosmicInputGradientLight = Brush.linearGradient(
    colors = listOf(BabyBlue, MintGlow.copy(alpha = 0.7f))
)

// Pastel Theme Gradients (Unchanged)
val PastelGradientDark = Brush.verticalGradient(
    colors = listOf(Charcoal, PastelLavender, LilacMist)
)
val PastelGradientLight = Brush.verticalGradient(
    colors = listOf(PearlWhite, PastelLavender, CottonCandy)
)
val PastelBubbleGradientDark = Brush.linearGradient(
    colors = listOf(PastelLavender, MintGlow.copy(alpha = 0.7f))
)
val PastelBubbleGradientLight = Brush.linearGradient(
    colors = listOf(CottonCandy, PaleMint.copy(alpha = 0.7f))
)
val PastelInputGradientDark = Brush.linearGradient(
    colors = listOf(LilacMist, DustyRose.copy(alpha = 0.7f))
)
val PastelInputGradientLight = Brush.linearGradient(
    colors = listOf(PastelLavender, SoftLemon.copy(alpha = 0.7f))
)

// Metallic Theme Gradients (Unchanged)
val MetallicGradientDark = Brush.verticalGradient(
    colors = listOf(OnyxBlack, SilverShine, RoseGold)
)
val MetallicGradientLight = Brush.verticalGradient(
    colors = listOf(Platinum, SilverShine, WarmBeige)
)
val MetallicBubbleGradientDark = Brush.linearGradient(
    colors = listOf(SilverShine, GoldGleam.copy(alpha = 0.7f))
)
val MetallicBubbleGradientLight = Brush.linearGradient(
    colors = listOf(WarmBeige, RoseGold.copy(alpha = 0.7f))
)
val MetallicInputGradientDark = Brush.linearGradient(
    colors = listOf(BronzeGlow, TitaniumGray.copy(alpha = 0.7f))
)
val MetallicInputGradientLight = Brush.linearGradient(
    colors = listOf(SilverShine, Platinum.copy(alpha = 0.7f))
)

// New Jewel Theme Gradients
val JewelGradientDark = Brush.verticalGradient(
    colors = listOf(OnyxGlow, SapphireBlue, EmeraldGreen)
)
val JewelGradientLight = Brush.verticalGradient(
    colors = listOf(LinenWhite, Aquamarine, SoftIvory)
)
val JewelBubbleGradientDark = Brush.linearGradient(
    colors = listOf(RubyRed, AmethystPurple.copy(alpha = 0.7f))
)
val JewelBubbleGradientLight = Brush.linearGradient(
    colors = listOf(Aquamarine, TopazYellow.copy(alpha = 0.7f))
)
val JewelInputGradientDark = Brush.linearGradient(
    colors = listOf(SapphireBlue, EmeraldGreen.copy(alpha = 0.7f))
)
val JewelInputGradientLight = Brush.linearGradient(
    colors = listOf(SoftIvory, Aquamarine.copy(alpha = 0.7f))
)

// New Minimal Theme Gradients
val MinimalGradientDark = Brush.verticalGradient(
    colors = listOf(CharredBlack, ShadowGray, Graphite)
)
val MinimalGradientLight = Brush.verticalGradient(
    colors = listOf(PearlWhite, FogGray, LinenWhite)
)
val MinimalBubbleGradientDark = Brush.linearGradient(
    colors = listOf(ShadowGray, CharcoalLight.copy(alpha = 0.7f))
)
val MinimalBubbleGradientLight = Brush.linearGradient(
    colors = listOf(CoolGray, StoneGray.copy(alpha = 0.7f))
)
val MinimalInputGradientDark = Brush.linearGradient(
    colors = listOf(Graphite, OnyxBlack.copy(alpha = 0.7f))
)
val MinimalInputGradientLight = Brush.linearGradient(
    colors = listOf(FogGray, PearlWhite.copy(alpha = 0.7f))
)

val VibrantAbstractGradientDark = Brush.verticalGradient(
    colors = listOf(GalacticBlack, CosmicIndigo, ElectricViolet)
)
val VibrantAbstractGradientLight = Brush.verticalGradient(
    colors = listOf(CloudPink, MintBurst, VividSky)
)
// New Response Gradients for AI Messages
val ResponseGradientDark = Brush.linearGradient(
    colors = listOf(CharredBlack, OnyxBlack.copy(alpha = 0.8f), ObsidianGray)
)
val ResponseGradientLight = Brush.linearGradient(
    colors = listOf(Graphite, ShadowGray.copy(alpha = 0.8f), Charcoal)
)
val ResponseGradientDarkMode = Brush.linearGradient(
    colors = listOf(CharredBlack, OnyxBlack.copy(alpha = 0.8f), ObsidianGray)
)
val ResponseGradientLightMode = Brush.linearGradient(
    colors = listOf(MistGray, SoftGray.copy(alpha = 0.8f), CloudWhite)
)

// Theme Composable
