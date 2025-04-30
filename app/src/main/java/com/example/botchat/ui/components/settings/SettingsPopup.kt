package com.example.botchat.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.SettingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheetBottom(
    viewModel: SettingViewModel,
    onDismiss: () -> Unit
) {
    val isDarkTheme = viewModel.darkModeEnabled
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            onDismiss()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = Transparent,
        tonalElevation = 8.dp,
        scrimColor = SlateBlack.copy(alpha = 0.6f)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .verticalScroll(rememberScrollState()),
            color = Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (isDarkTheme) SettingsBackgroundGradientDark
                        else SettingsBackgroundGradientLight,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = SettingsBorderGradient,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = if (isDarkTheme) PureWhite else SlateBlack,
                            fontSize = 24.sp
                        )
                    )
                    var isPressed by remember { mutableStateOf(false) }
                    val transition = updateTransition(targetState = isPressed, label = "ButtonScale")
                    val buttonScale by transition.animateFloat(
                        transitionSpec = { tween(150) },
                        label = "Scale"
                    ) { pressed -> if (pressed) 0.9f else 1f }

                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }
                            onDismiss()
                        },
                        modifier = Modifier
                            .scale(buttonScale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                    }
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkTheme) ErrorRed else Pink40,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                var selectedTab by remember { mutableStateOf(0) }
                val tabs = listOf("General", "API", "Other")
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Transparent,
                    contentColor = if (isDarkTheme) PureWhite else SlateBlack,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = if (isDarkTheme) ElectricCyan else Purple40
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (selectedTab == index) {
                                            if (isDarkTheme) ElectricCyan else Purple40
                                        } else {
                                            if (isDarkTheme) GalacticGray else SlateBlack.copy(alpha = 0.7f)
                                        }
                                    )
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300)) },
                    label = "SettingsTabTransition"
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> GeneralSettingsTab(
                            darkModeEnabled = viewModel.darkModeEnabled,
                            notificationsEnabled = viewModel.notificationsEnabled,
                            historyRetentionDays = viewModel.historyRetentionDays,
                            onDarkModeToggle = { viewModel.toggleDarkMode() },
                            onNotificationsToggle = { viewModel.toggleNotifications() },
                            onRetentionChange = { viewModel.updateHistoryRetentionDays(it) }
                        )
                        1 -> ApiSettingsTab(
                            apiKey = viewModel.apiKey,
                            serverUrl = viewModel.serverUrl,
                            showAdvancedSettings = viewModel.showAdvancedSettings,
                            cachingEnabled = viewModel.cachingEnabled,
                            showApiKey = viewModel.showApiKey,
                            onApiKeyChange = { viewModel.updateApiKey(it) },
                            onServerUrlChange = { viewModel.updateServerUrl(it) },
                            onAdvancedSettingsToggle = { viewModel.toggleAdvancedSettings() },
                            onCachingToggle = { viewModel.toggleCaching() },
                            onApiKeyVisibilityToggle = { viewModel.toggleApiKeyVisibility() }
                        )
                        2 -> OtherSettingsTab(
                            soundEffectsEnabled = viewModel.soundEffectsEnabled,
                            analyticsEnabled = viewModel.analyticsEnabled,
                            onSoundEffectsToggle = { viewModel.toggleSoundEffects() },
                            onAnalyticsToggle = { viewModel.toggleAnalytics() }
                        )
                    }
                }
            }
        }
    }
}