package com.example.ChatBlaze.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GeneralSettingsTab(
    notificationsEnabled: Boolean,
    historyRetentionDays: Int,
    systemPrompt: String,
    onNotificationsToggle: () -> Unit,
    onRetentionChange: (Int) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(animationSpec = tween(300)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "General Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SettingsSwitchItem(
            label = "Notifications",
            checked = notificationsEnabled,
            onCheckedChange = { onNotificationsToggle() }
        )

        SettingsRetentionItem(
            days = historyRetentionDays,
            onDaysChange = onRetentionChange
        )

        SystemPromptInput(
            systemPrompt = systemPrompt,
            onSystemPromptChange = onSystemPromptChange
        )
    }
}

@Composable
fun SystemPromptInput(
    systemPrompt: String,
    onSystemPromptChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = systemPrompt,
            onValueChange = onSystemPromptChange,
            label = { Text("System Prompt", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(20.dp),
            supportingText = if (systemPrompt.isBlank()) {
                { Text("System prompt is required", color = MaterialTheme.colorScheme.error) }
            } else null
        )
    }
}