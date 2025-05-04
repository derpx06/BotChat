package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun HuggingFaceSettingsSubTab(
    apiKey: String,
    serverUrl: String,
    selectedModel: String,
    showApiKey: Boolean,
    onApiKeyChange: (String) -> Unit,
    onServerUrlChange: (String) -> Unit,
    onSelectedModelChange: (String) -> Unit,
    onApiKeyVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "HuggingFace Configuration",
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 18.sp
            )
        )
        ApiKeyInput(
            apiKey = apiKey,
            showApiKey = showApiKey,
            onApiKeyChange = onApiKeyChange,
            onVisibilityToggle = onApiKeyVisibilityToggle,
            label = "HuggingFace API Key"
        )
        ServerUrlInput(
            serverUrl = serverUrl,
            onServerUrlChange = onServerUrlChange
        )
        ModelSelectionInput(
            selectedModel = selectedModel,
            onModelChange = onSelectedModelChange,
            models = listOf(
                "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",
                "microsoft/phi-3-mini-4k-instruct",
                "google/gemma-2b-it",
                "Qwen/Qwen3-0.6B",
                "NousResearch/Hermes-2-Pro-Mistral-7B",
                "facebook/blenderbot-400M-distill"
            )
        )
    }
}

@Composable
fun ServerUrlInput(
    serverUrl: String,
    onServerUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = serverUrl,
            onValueChange = onServerUrlChange,
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp),
            supportingText = if (serverUrl.isBlank()) {
                { Text("Server URL is required", color = MaterialTheme.colorScheme.error) }
            } else null
        )
    }
}