package com.example.botchat.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.SettingViewModel

@Composable
fun SettingsPopup(
    viewModel: SettingViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.background == DeepSpaceBlack
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "API", "Other")

    AnimatedVisibility(
        visible = viewModel.showSettings,
        modifier = modifier,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDarkTheme) SettingsBackgroundGradientDark else SettingsBackgroundGradientLight)
                .border(1.dp, SettingsBorderGradient, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                            color = if (isDarkTheme) StarlightWhite else Black,
                            fontSize = 24.sp
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkTheme) ErrorRed else Pink40
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Transparent,
                    contentColor = if (isDarkTheme) StarlightWhite else Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = if (isDarkTheme) NeonCyan else Purple40
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
                                            if (isDarkTheme) NeonCyan else Purple40
                                        } else {
                                            if (isDarkTheme) GalacticGray else Black.copy(alpha = 0.7f)
                                        }
                                    )
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                when (selectedTab) {
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