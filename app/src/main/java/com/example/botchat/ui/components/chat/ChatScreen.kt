package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.database.ChatDatabase
import com.example.botchat.database.modelDatabase.modelDao
import com.example.botchat.database.modelDatabase.modelDatabase
import com.example.botchat.ui.components.TopBar
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.Chat.ChatViewModel
import com.example.botchat.viewmodel.Chat.ChatViewModelFactory
import com.example.botchat.viewmodel.setting.SettingViewModel
import com.example.botchat.viewmodel.setting.SettingViewModelFactory

@Composable
fun ChatScreen(
    modelDao: modelDao,
    chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            UserSettingsDataStore(LocalContext.current),
            ChatRepository(ChatDatabase.getDatabase(LocalContext.current).chatDao())
        )
    ),
    settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(UserSettingsDataStore(LocalContext.current))
    ),
    onNavigateToModels: () -> Unit = {}
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val selectedTheme by settingViewModel.theme.collectAsStateWithLifecycle(initialValue = "gradient")
    val showSettings = settingViewModel.showSettings
    val context = LocalContext.current
    val modelDao = remember { modelDatabase.getDatabase(context).modelDao() }

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
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SolidColor(if (isDarkTheme) MidnightBlack else CloudWhite),
                    alpha = 0.85f
                )
                .border(
                    width = 0.5.dp,
                    brush = if (isDarkTheme) Brush.linearGradient(listOf(NeonBlue, GalacticGray)) else Brush.linearGradient(listOf(Aquamarine, CoolGray)),
                    shape = RoundedCornerShape(16.dp)
                )
                .safeDrawingPadding()
                .statusBarsPadding(),
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
                    onSettingsClick = { settingViewModel.toggleSettings() },
                    onModelsClick = onNavigateToModels,
                    onDeleteClick = { chatViewModel.clearMessages() },
                    isDarkTheme = isDarkTheme
                )
                ChatMessages(
                    messages = uiState.messages,
                    isLoading = uiState.isLoading,
                    theme = selectedTheme,
                    modifier = Modifier
                        .weight(1f)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .windowInsetsPadding(WindowInsets.ime)
                    .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
                    .offset(y = (-PaddingMedium))
                    .align(Alignment.BottomCenter)
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
                            //modifier = Modifier.align(Alignment.Center)
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
                        //modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

// Padding Constants
private val PaddingTiny = 4.dp
private val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp