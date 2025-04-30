package com.example.botchat.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Other Settings",
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (MaterialTheme.colorScheme.background ==MidnightBlack ) PureWhite else SlateBlack,
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

@Composable
private fun SettingsSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
    }
}