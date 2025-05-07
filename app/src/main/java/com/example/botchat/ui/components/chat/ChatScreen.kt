package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.database.ChatDatabase
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.ChatViewModel
import com.example.botchat.viewmodel.ChatViewModelFactory
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory

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
    val isDarkTheme by settingViewModel.darkModeEnabled.collectAsStateWithLifecycle(initialValue = false)
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
            ChatInputSection(
                inputText = uiState.inputText,
                onInputChange = chatViewModel::updateInputText,
                onSendClick = chatViewModel::sendMessage,
                onStopClick = chatViewModel::cancelProcessing,
                isLoading = uiState.isLoading,
                isDarkTheme = isDarkTheme
            )
            Button(
                onClick = { chatViewModel.clearMessages() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                    contentColor = if (isDarkTheme) MidnightBlack else PureWhite
                )
            ) {
                Text("Clear Chat")
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