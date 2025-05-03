package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.ChatViewModel
import com.example.botchat.viewmodel.ChatViewModelFactory
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(UserSettingsDataStore(LocalContext.current))),
    settingViewModel: SettingViewModel = viewModel(factory = SettingViewModelFactory(UserSettingsDataStore(LocalContext.current)))
) {
    val uiState = chatViewModel.uiState.collectAsState().value
    val isDarkTheme = settingViewModel.darkModeEnabled.collectAsState(initial = false).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) MidnightBlack else CloudWhite)
            .imePadding()
            .safeDrawingPadding()
            .statusBarsPadding()
    ) {
        if (uiState.showErrorPage) {
            ErrorPage(
                isDarkTheme = isDarkTheme,
                onDismiss = chatViewModel::clearError
            )
        } else {
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
        }

        AnimatedVisibility(
            visible = uiState.errorMessage != null && !uiState.showErrorPage,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
        ) {
            uiState.errorMessage?.let { errorMessage ->
                ErrorDialog(
                    errorMessage = errorMessage,
                    onDismiss = chatViewModel::clearError,
                    isDarkTheme = isDarkTheme
                )
            }
        }

        AnimatedVisibility(
            visible = settingViewModel.showSettings,
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