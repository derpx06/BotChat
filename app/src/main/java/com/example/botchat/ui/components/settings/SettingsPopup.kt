package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.SettingViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheetBottom(
    viewModel: SettingViewModel,
    onDismiss: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycleOwner = lifecycleOwner
    )
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(
        initialValue = true,
        lifecycleOwner = lifecycleOwner
    )
    val cachingEnabled by viewModel.cachingEnabled.collectAsStateWithLifecycle(
        initialValue = true,
        lifecycleOwner = lifecycleOwner
    )
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycleOwner = lifecycleOwner
    )
    val openRouterApiKey by viewModel.openRouterApiKey.collectAsStateWithLifecycle(
        initialValue = "",
        lifecycleOwner = lifecycleOwner
    )
    val openRouterModel by viewModel.openRouterModel.collectAsStateWithLifecycle(
        initialValue = "google/gemma-3-12b-it:free",
        lifecycleOwner = lifecycleOwner
    )
    val huggingFaceApiKey by viewModel.huggingFaceApiKey.collectAsStateWithLifecycle(
        initialValue = "",
        lifecycleOwner = lifecycleOwner
    )
    val huggingFaceModel by viewModel.selectedModel.collectAsStateWithLifecycle(
        initialValue = "facebook/blenderbot-400M-distill",
        lifecycleOwner = lifecycleOwner
    )
    val apiEndpoint by viewModel.apiEndpoint.collectAsStateWithLifecycle(
        initialValue = "https://api-inference.huggingface.co",
        lifecycleOwner = lifecycleOwner
    )
    val historyRetentionDays by viewModel.historyRetentionDays.collectAsStateWithLifecycle(
        initialValue = 7,
        lifecycleOwner = lifecycleOwner
    )
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycleOwner = lifecycleOwner
    )
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle(
        initialValue = "openrouter",
        lifecycleOwner = lifecycleOwner
    )
    val systemPrompt by viewModel.systemPrompt.collectAsStateWithLifecycle(
        initialValue = "You are a helpful assistant.",
        lifecycleOwner = lifecycleOwner
    )

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    var showOpenRouterApiKey by remember { mutableStateOf(false) }
    var showHuggingFaceApiKey by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = if (darkModeEnabled) StarlitPurple else MistGray,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(16.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        color = if (darkModeEnabled) PureWhite else SlateBlack
                    )
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Transparent,
                    contentColor = if (darkModeEnabled) ElectricCyan else Purple40,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .clip(RoundedCornerShape(50)),
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
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        color = if (selectedTabIndex == index) {
                                            if (darkModeEnabled) ElectricCyan else Purple40
                                        } else {
                                            if (darkModeEnabled) PureWhite.copy(alpha = 0.7f) else SlateBlack.copy(alpha = 0.7f)
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
                        darkModeEnabled = darkModeEnabled,
                        notificationsEnabled = notificationsEnabled,
                        historyRetentionDays = historyRetentionDays,
                        systemPrompt = systemPrompt,
                        onDarkModeToggle = { viewModel.updateDarkMode(it) },
                        onNotificationsToggle = { viewModel.updateNotificationsEnabled(!notificationsEnabled) },
                        onRetentionChange = { viewModel.updateHistoryRetentionDays(it) },
                        onSystemPromptChange = { viewModel.updateSystemPrompt(it) },
                        modifier = Modifier.weight(1f)
                    )
                    2 -> OtherSettingsTab(
                        soundEffectsEnabled = soundEffectsEnabled,
                        analyticsEnabled = analyticsEnabled,
                        onSoundEffectsToggle = { viewModel.updateSoundEffectsEnabled(it) },
                        onAnalyticsToggle = { viewModel.updateAnalyticsEnabled(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            if (selectedProvider == "huggingface" && (huggingFaceApiKey.isBlank() || apiEndpoint.isBlank() || huggingFaceModel.isBlank())) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all required HuggingFace fields")
                                }
                            } else if (selectedProvider == "openrouter" && (openRouterApiKey.isBlank() || openRouterModel.isBlank())) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all required OpenRouter fields")
                                }
                            } else if (systemPrompt.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("System prompt is required")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Settings saved")
                                    onDismiss()
                                }
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (darkModeEnabled) ElectricCyan else Purple40
                        )
                    ) {
                        Text("Save")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (darkModeEnabled) PureWhite else SlateBlack
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}