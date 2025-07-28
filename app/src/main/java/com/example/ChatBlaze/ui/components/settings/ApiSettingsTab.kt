package com.example.ChatBlaze.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ChatBlaze.data.database.modelDatabase.modelDao
import com.example.ChatBlaze.ui.theme.Transparent
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel

@Composable
fun ApiSettingsTab(
    settingViewModel: SettingViewModel,
    modelDao: modelDao,
    onNavigateToModels: () -> Unit,
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
    val selectedProvider by settingViewModel.selectedProvider.collectAsStateWithLifecycle(initialValue = "openrouter")
    val cachingEnabled by settingViewModel.cachingEnabled.collectAsStateWithLifecycle(initialValue = true)
    var showAdvancedSettings by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(animationSpec = tween(300)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "API Configuration",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ProviderSelectionInput(
            selectedProvider = selectedProvider,
            onProviderChange = { settingViewModel.updateSelectedProvider(it) }
        )
        var selectedSubTabIndex by remember { mutableStateOf(if (selectedProvider == "openrouter") 0 else 1) }
        LaunchedEffect(selectedProvider) {
            selectedSubTabIndex = if (selectedProvider == "openrouter") 0 else 1
        }
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            TabRow(
                selectedTabIndex = selectedSubTabIndex,
                containerColor = Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedSubTabIndex])
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        height = 4.dp
                    )
                }
            ) {
                listOf("OpenRouter", "HuggingFace").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTabIndex == index,
                        onClick = { selectedSubTabIndex = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedSubTabIndex == index) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    }
                                )
                            )
                        }
                    )
                }
            }
        }
        when (selectedSubTabIndex) {
            0 -> OpenRouterSettingsSubTab(
                settingViewModel = settingViewModel,
                modelDao = modelDao,
                onNavigateToModels = onNavigateToModels,
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
            onAdvancedSettingsToggle = { showAdvancedSettings = !showAdvancedSettings },
            onCachingToggle = { settingViewModel.updateCachingEnabled(it) }
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
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedProvider.replaceFirstChar { it.uppercase() },
            onValueChange = { /* Read-only */ },
            label = { Text("Service Provider", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Provider", tint = MaterialTheme.colorScheme.onSurface)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.95f)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
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
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Advanced Settings",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = showAdvancedSettings,
                onCheckedChange = { onAdvancedSettingsToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    checkmarkColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        AnimatedVisibility(
            visible = showAdvancedSettings,
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Enable Caching",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = cachingEnabled,
                    onCheckedChange = onCachingToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}