package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun OtherSettingsTab(
    soundEffectsEnabled: Boolean,
    analyticsEnabled: Boolean,
    onSoundEffectsToggle: (Boolean) -> Unit,
    onAnalyticsToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Other Settings",
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 20.sp
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
    }
}