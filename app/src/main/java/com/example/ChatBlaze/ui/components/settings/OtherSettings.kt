package com.example.ChatBlaze.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.ui.theme.*

@Composable
fun OtherSettingsTab(
    soundEffectsEnabled: Boolean,
    analyticsEnabled: Boolean,
    selectedTheme: String,
    darkModeSetting: String,
    onSoundEffectsToggle: (Boolean) -> Unit,
    onAnalyticsToggle: (Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onDarkModeChange: (String) -> Unit,
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
            DarkModeSelectionSection(
                darkModeSetting = darkModeSetting,
                onDarkModeChange = onDarkModeChange
            )
        }
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
fun DarkModeSelectionSection(
    darkModeSetting: String,
    onDarkModeChange: (String) -> Unit,
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
            text = "Dark Mode",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 16.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("System", "Dark", "Light").forEach { mode ->
                DarkModeBox(
                    mode = mode,
                    isSelected = darkModeSetting == mode.lowercase(),
                    onClick = { onDarkModeChange(mode.lowercase()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DarkModeBox(
    mode: String,
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
            text = mode,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isDarkTheme) PureWhite else SlateBlack,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
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
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
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

@Composable
fun SettingsSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 16.sp
            )
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40,
                checkedTrackColor = if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan.copy(alpha = 0.5f) else Purple40.copy(alpha = 0.5f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        )
    }
}