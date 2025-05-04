package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun ApiSettingsTab(
    selectedProvider: String,
    openRouterApiKey: String,
    openRouterModel: String,
    huggingFaceApiKey: String,
    huggingFaceServerUrl: String,
    huggingFaceModel: String,
    showAdvancedSettings: Boolean,
    cachingEnabled: Boolean,
    showOpenRouterApiKey: Boolean,
    showHuggingFaceApiKey: Boolean,
    onSelectedProviderChange: (String) -> Unit,
    onOpenRouterApiKeyChange: (String) -> Unit,
    onOpenRouterModelChange: (String) -> Unit,
    onHuggingFaceApiKeyChange: (String) -> Unit,
    onHuggingFaceServerUrlChange: (String) -> Unit,
    onHuggingFaceModelChange: (String) -> Unit,
    onAdvancedSettingsToggle: () -> Unit,
    onCachingToggle: (Boolean) -> Unit,
    onOpenRouterApiKeyVisibilityToggle: () -> Unit,
    onHuggingFaceApiKeyVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "API Configuration",
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 20.sp
            )
        )
        ProviderSelectionInput(
            selectedProvider = selectedProvider,
            onProviderChange = onSelectedProviderChange
        )
        var selectedSubTabIndex by remember { mutableStateOf(if (selectedProvider == "openrouter") 0 else 1) }
        LaunchedEffect(selectedProvider) {
            selectedSubTabIndex = if (selectedProvider == "openrouter") 0 else 1
        }
        TabRow(
            selectedTabIndex = selectedSubTabIndex,
            containerColor = Transparent,
            contentColor = if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .shadow(2.dp)
        ) {
            listOf("OpenRouter", "HuggingFace").forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTabIndex == index,
                    onClick = { selectedSubTabIndex = index },
                    text = {
                        Text(
                            title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = if (selectedSubTabIndex == index) {
                                    if (MaterialTheme.colorScheme.background == MidnightBlack) ElectricCyan else Purple40
                                } else {
                                    if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite.copy(alpha = 0.7f) else SlateBlack.copy(alpha = 0.7f)
                                }
                            )
                        )
                    }
                )
            }
        }
        when (selectedSubTabIndex) {
            0 -> OpenRouterSettingsSubTab(
                apiKey = openRouterApiKey,
                selectedModel = openRouterModel,
                showApiKey = showOpenRouterApiKey,
                onApiKeyChange = onOpenRouterApiKeyChange,
                onSelectedModelChange = onOpenRouterModelChange,
                onApiKeyVisibilityToggle = onOpenRouterApiKeyVisibilityToggle,
                modifier = Modifier.fillMaxWidth()
            )
            1 -> HuggingFaceSettingsSubTab(
                apiKey = huggingFaceApiKey,
                serverUrl = huggingFaceServerUrl,
                selectedModel = huggingFaceModel,
                showApiKey = showHuggingFaceApiKey,
                onApiKeyChange = onHuggingFaceApiKeyChange,
                onServerUrlChange = onHuggingFaceServerUrlChange,
                onSelectedModelChange = onHuggingFaceModelChange,
                onApiKeyVisibilityToggle = onHuggingFaceApiKeyVisibilityToggle,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AdvancedSettingsSection(
            showAdvancedSettings = showAdvancedSettings,
            cachingEnabled = cachingEnabled,
            onAdvancedSettingsToggle = onAdvancedSettingsToggle,
            onCachingToggle = onCachingToggle
        )
    }
}

@Composable
fun ProviderSelectionInput(
    selectedProvider: String,
    onProviderChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val providers = listOf("openrouter", "huggingface")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = selectedProvider.replaceFirstChar { it.uppercase() },
            onValueChange = { /* Read-only */ },
            label = { Text("Service Provider") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Provider")
                }
            },
        colors = TextFieldDefaults.colors(
    //  containerColor = Transparent,
     focusedIndicatorColor = MaterialTheme.colorScheme.primary,
       //unfocusedIndicator = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
             //= MaterialTheme.colorScheme.error,
       cursorColor = MaterialTheme.colorScheme.onSurface,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        errorLabelColor = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        //textColor = MaterialTheme.colorScheme.onSurface, // Added for text color
        //placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        ),
            shape = RoundedCornerShape(8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .shadow(4.dp)
        ) {
            providers.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onProviderChange(provider)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AdvancedSettingsSection(
    showAdvancedSettings: Boolean,
    cachingEnabled: Boolean,
    onAdvancedSettingsToggle: () -> Unit,
    onCachingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Advanced Settings",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = showAdvancedSettings,
                onCheckedChange = { onAdvancedSettingsToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    checkmarkColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        if (showAdvancedSettings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Enable Caching",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = cachingEnabled,
                    onCheckedChange = onCachingToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}