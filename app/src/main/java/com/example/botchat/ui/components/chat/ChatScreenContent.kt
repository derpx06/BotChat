package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.database.modelDatabase.modelDao
import com.example.botchat.ui.components.TopBar
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.Chat.ChatViewModel
import com.example.botchat.viewmodel.setting.SettingViewModel

// Padding Constants
private val PaddingTiny = 4.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp

@Composable
fun ChatScreenContent(
    chatViewModel: ChatViewModel,
    settingViewModel: SettingViewModel,
    onNavigateToModels: () -> Unit,
    onDrawerClicked: () -> Unit,
    modelDao: modelDao,
    modifier: Modifier = Modifier
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val selectedTheme by settingViewModel.theme.collectAsStateWithLifecycle(initialValue = "gradient")
    val showSettings by remember { derivedStateOf { settingViewModel.showSettings } }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { it / 10 },
            animationSpec = spring(dampingRatio = 0.7f)
        ) + expandVertically(animationSpec = spring(dampingRatio = 0.7f)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
            targetOffsetY = { it / 10 },
            animationSpec = spring(dampingRatio = 0.7f)
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = SolidColor(if (isDarkTheme) MidnightBlack else CloudWhite),
                    alpha = 0.9f
                )
                .border(
                    width = 0.5.dp,
                    brush = if (isDarkTheme) Brush.linearGradient(listOf(NeonBlue, ElectricCyan)) else Brush.linearGradient(listOf(Aquamarine, Purple40)),
                    shape = RoundedCornerShape(16.dp)
                )
                .safeContentPadding(), // Handles status, navigation, and other system insets
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = PaddingExtraSmall),
                verticalArrangement = Arrangement.spacedBy(PaddingExtraSmall)
            ) {
                TopBar(
                    onMenuClick = onDrawerClicked,
                    isDarkTheme = isDarkTheme
                )
                ChatMessages(
                    messages = uiState.messages,
                    isLoading = uiState.isLoading,
                    theme = selectedTheme,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = PaddingLarge * 2 + PaddingMedium), // 40.dp for visibility
                    isDarkTheme = isDarkTheme
                )
            }

            ChatInputSection(
                inputText = uiState.inputText,
                onInputChange = chatViewModel::updateInputText,
                onSendClick = chatViewModel::sendMessage,
                onStopClick = chatViewModel::cancelProcessing,
                isLoading = uiState.isLoading,
                isDarkTheme = isDarkTheme,
                useGradientTheme = selectedTheme == "gradient",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .windowInsetsPadding(WindowInsets.ime) // Sticks to keyboard
                    .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
                    .align(Alignment.BottomCenter), // Explicitly align to bottom
                photo_supported = false
            )

            AnimatedVisibility(
                visible = uiState.showErrorDialog && uiState.errorMessage != null,
                enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(dampingRatio = 0.8f)
                ),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.9f)
            ) {
                uiState.errorMessage?.let { errorMessage ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (isDarkTheme) MidnightBlack.copy(alpha = 0.7f) else CloudWhite.copy(alpha = 0.7f)
                            )
                            .border(
                                width = 0.75.dp,
                                brush = if (isDarkTheme) Brush.linearGradient(listOf(NeonBlue, ElectricCyan)) else Brush.linearGradient(listOf(Aquamarine, Purple40)),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        ErrorDialog(
                            errorMessage = errorMessage,
                            isDarkTheme = isDarkTheme,
                            onDismiss = chatViewModel::clearError,
                            onRetry = uiState.retryAction,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .background(
                                    color = if (isDarkTheme) MidnightBlack.copy(alpha = 0.95f) else CloudWhite.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showSettings,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = 0.8f)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(dampingRatio = 0.8f)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (isDarkTheme) MidnightBlack.copy(alpha = 0.7f) else CloudWhite.copy(alpha = 0.7f)
                        )
                        .border(
                            width = 0.75.dp,
                            brush = if (isDarkTheme) Brush.linearGradient(listOf(NeonBlue, ElectricCyan)) else Brush.linearGradient(listOf(Aquamarine, Purple40)),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    SettingsSheetBottom(
                        viewModel = settingViewModel,
                        onDismiss = { settingViewModel.toggleSettings() },
                        onNavigateToModels = onNavigateToModels,
                        modelDao = modelDao,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(
                                color = if (isDarkTheme) MidnightBlack.copy(alpha = 0.95f) else CloudWhite.copy(alpha = 0.95f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}