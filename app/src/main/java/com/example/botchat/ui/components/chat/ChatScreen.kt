package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.R
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.database.ChatDatabase
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.Chat.ChatViewModel
import com.example.botchat.viewmodel.Chat.ChatViewModelFactory
import com.example.botchat.viewmodel.setting.SettingViewModel
import com.example.botchat.viewmodel.setting.SettingViewModelFactory

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            UserSettingsDataStore(LocalContext.current),
            ChatRepository(ChatDatabase.getDatabase(LocalContext.current).chatDao())
        )
    ),
    settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(UserSettingsDataStore(LocalContext.current))
    )
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val selectedTheme by settingViewModel.theme.collectAsStateWithLifecycle(initialValue = "gradient")
    val showSettings = settingViewModel.showSettings

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) MidnightBlack else CloudWhite)
            .imePadding()
            .safeDrawingPadding()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                onSettingsClick = { settingViewModel.toggleSettings() },
                isDarkTheme = isDarkTheme
            )
            ChatMessages(
                messages = uiState.messages,
                isLoading = uiState.isLoading,
                theme = selectedTheme,
                modifier = Modifier.weight(1f),
                isDarkTheme = isDarkTheme
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChatInputSection(
                    inputText = uiState.inputText,
                    onInputChange = chatViewModel::updateInputText,
                    onSendClick = chatViewModel::sendMessage,
                    onStopClick = chatViewModel::cancelProcessing,
                    isLoading = uiState.isLoading,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { chatViewModel.clearMessages() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = "Clear Chat",
                        tint = if (isDarkTheme) ElectricCyan else Purple40
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.showErrorDialog && uiState.errorMessage != null,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
        ) {
            uiState.errorMessage?.let { errorMessage ->
                ErrorDialog(
                    errorMessage = errorMessage,
                    isDarkTheme = isDarkTheme,
                    onDismiss = chatViewModel::clearError,
                    onRetry = uiState.retryAction
                )
            }
        }

        AnimatedVisibility(
            visible = showSettings,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(400)),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(400))
        ) {
            SettingsSheetBottom(
                viewModel = settingViewModel,
                onDismiss = { settingViewModel.toggleSettings() }
            )
        }
    }
}