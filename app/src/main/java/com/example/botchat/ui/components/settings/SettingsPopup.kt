package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheetBottom(
    viewModel: SettingViewModel,
    onDismiss: () -> Unit
) {
    val darkModeEnabled = viewModel.getDarkModeEnabled()
    val darkModeSetting by viewModel.darkModeSetting.collectAsStateWithLifecycle(initialValue = "system")
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(initialValue = true)
    val cachingEnabled by viewModel.cachingEnabled.collectAsStateWithLifecycle(initialValue = true)
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsStateWithLifecycle(initialValue = false)
    val openRouterApiKey by viewModel.openRouterApiKey.collectAsStateWithLifecycle(initialValue = "")
    val openRouterModel by viewModel.openRouterModel.collectAsStateWithLifecycle(initialValue = "google/gemma-3-12b-it:free")
    val huggingFaceApiKey by viewModel.huggingFaceApiKey.collectAsStateWithLifecycle(initialValue = "")
    val huggingFaceModel by viewModel.selectedModel.collectAsStateWithLifecycle(initialValue = "facebook/blenderbot-400M-distill")
    val apiEndpoint by viewModel.apiEndpoint.collectAsStateWithLifecycle(initialValue = "https://api-inference.huggingface.co")
    val historyRetentionDays by viewModel.historyRetentionDays.collectAsStateWithLifecycle(initialValue = 7)
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsStateWithLifecycle(initialValue = false)
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle(initialValue = "openrouter")
    val systemPrompt by viewModel.systemPrompt.collectAsStateWithLifecycle(initialValue = "You are a helpful assistant.")
    val selectedTheme by viewModel.theme.collectAsStateWithLifecycle(initialValue = "gradient")

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    var showOpenRouterApiKey by remember { mutableStateOf(false) }
    var showHuggingFaceApiKey by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = BottomSheetDefaults.Elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selectedTheme == "plain") {
                        Modifier.background(if (darkModeEnabled) MidnightBlack else CloudWhite)
                    } else {
                        Modifier.background(
                            brush = if (darkModeEnabled) SettingsBackgroundGradientDark else SettingsBackgroundGradientLight
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(20.dp)
                    .animateContentSize(animationSpec = tween(300)),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    contentColor = if (darkModeEnabled) ElectricCyan else Purple40,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .height(4.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = if (darkModeEnabled) ElectricCyan else Purple40
                        )
                    }
                ) {
                    listOf("API", "General", "Other").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 18.sp,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) {
                                            if (darkModeEnabled) ElectricCyan else Purple40
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        }
                                    )
                                )
                            }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> ApiSettingsTab(
                        selectedProvider = selectedProvider,
                        openRouterApiKey = openRouterApiKey,
                        openRouterModel = openRouterModel,
                        huggingFaceApiKey = huggingFaceApiKey,
                        huggingFaceServerUrl = apiEndpoint,
                        huggingFaceModel = huggingFaceModel,
                        showAdvancedSettings = showAdvancedSettings,
                        cachingEnabled = cachingEnabled,
                        showOpenRouterApiKey = showOpenRouterApiKey,
                        showHuggingFaceApiKey = showHuggingFaceApiKey,
                        onSelectedProviderChange = { viewModel.updateSelectedProvider(it) },
                        onOpenRouterApiKeyChange = { viewModel.updateOpenRouterApiKey(it) },
                        onOpenRouterModelChange = { viewModel.updateOpenRouterModel(it) },
                        onHuggingFaceApiKeyChange = { viewModel.updateHuggingFaceApiKey(it) },
                        onHuggingFaceServerUrlChange = { viewModel.updateApiEndpoint(it) },
                        onHuggingFaceModelChange = { viewModel.updateSelectedModel(it) },
                        onAdvancedSettingsToggle = { showAdvancedSettings = !showAdvancedSettings },
                        onCachingToggle = { viewModel.updateCachingEnabled(it) },
                        onOpenRouterApiKeyVisibilityToggle = { showOpenRouterApiKey = !showOpenRouterApiKey },
                        onHuggingFaceApiKeyVisibilityToggle = { showHuggingFaceApiKey = !showHuggingFaceApiKey },
                        modifier = Modifier.weight(1f)
                    )
                    1 -> GeneralSettingsTab(
                        notificationsEnabled = notificationsEnabled,
                        historyRetentionDays = historyRetentionDays,
                        systemPrompt = systemPrompt,
                        onNotificationsToggle = { viewModel.updateNotificationsEnabled(!notificationsEnabled) },
                        onRetentionChange = { viewModel.updateHistoryRetentionDays(it) },
                        onSystemPromptChange = { viewModel.updateSystemPrompt(it) },
                        modifier = Modifier.weight(1f)
                    )
                    2 -> OtherSettingsTab(
                        soundEffectsEnabled = soundEffectsEnabled,
                        analyticsEnabled = analyticsEnabled,
                        selectedTheme = selectedTheme,
                        darkModeSetting = darkModeSetting,
                        onSoundEffectsToggle = { viewModel.updateSoundEffectsEnabled(it) },
                        onAnalyticsToggle = { viewModel.updateAnalyticsEnabled(it) },
                        onThemeChange = { viewModel.updateTheme(it) },
                        onDarkModeChange = { viewModel.updateDarkModeSetting(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}