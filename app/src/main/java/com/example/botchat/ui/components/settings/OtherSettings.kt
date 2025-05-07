package com.example.botchat.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun OtherSettingsTab(
    soundEffectsEnabled: Boolean,
    analyticsEnabled: Boolean,
    selectedTheme: String,
    onSoundEffectsToggle: (Boolean) -> Unit,
    onAnalyticsToggle: (Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp)
            .animateContentSize(animationSpec = tween(400)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Other Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        SettingsSwitchItem(
            label = "Sound Effects",
            checked = soundEffectsEnabled,
            onCheckedChange = onSoundEffectsToggle
        )
        SettingsSwitchItem(
            label = "Analytics Tracking",
            checked = analyticsEnabled,
            onCheckedChange = onAnalyticsToggle
        )
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.95f)
        ) {
            ThemeSelectionSection(
                selectedTheme = selectedTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
fun ThemeSelectionSection(
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Themes",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Plain", "Gradient", "Mixed").forEach { theme ->
                ThemeBox(
                    theme = theme,
                    isSelected = selectedTheme == theme.lowercase(),
                    onClick = { onThemeChange(theme.lowercase()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ThemeBox(
    theme: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.background == MidnightBlack
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    if (isDarkTheme) ElectricCyan else Purple40
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = theme,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isDarkTheme) PureWhite else SlateBlack,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}