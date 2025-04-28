package com.example.botchat.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.ui.components.settings.SettingsPopup
import com.example.botchat.ui.theme.BackgroundGradientDark
import com.example.botchat.ui.theme.BackgroundGradientLight
import com.example.botchat.ui.theme.DeepSpaceBlack
import com.example.botchat.viewmodel.ChatViewModel
import com.example.botchat.viewmodel.SettingViewModel

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(),
    settingViewModel: SettingViewModel = viewModel()
) {
    val uiState = chatViewModel.uiState.collectAsState().value
    val isDarkTheme = MaterialTheme.colorScheme.background == DeepSpaceBlack

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) BackgroundGradientDark else BackgroundGradientLight)
            .imePadding()
            .safeDrawingPadding()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                onSettingsClick = { settingViewModel.toggleSettings() },
                isDarkTheme = isDarkTheme
            )
            ChatMessages(
                messages = uiState.messages,
                isLoading = uiState.isLoading,
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
        }

        if (uiState.errorMessage != null) {
            ErrorDialog(
                errorMessage = uiState.errorMessage!!,
                onDismiss = chatViewModel::clearError,
                isDarkTheme = isDarkTheme
            )
        }

        if (settingViewModel.showSettings) {
            SettingsPopup(
                viewModel = settingViewModel,
                onDismiss = { settingViewModel.toggleSettings() },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}