package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.ChatViewModel
import com.example.botchat.viewmodel.ChatViewModelFactory
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.botchat.ui.components.settings.SettingsSheetBottom
import com.example.botchat.ui.theme.CloudWhite
import com.example.botchat.ui.theme.MidnightBlack

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(UserSettingsDataStore(LocalContext.current))),
    settingViewModel: SettingViewModel = viewModel(factory = SettingViewModelFactory(UserSettingsDataStore(LocalContext.current)))
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme by settingViewModel.darkModeEnabled.collectAsStateWithLifecycle(initialValue = false)
    val showSettings  = settingViewModel.showSettings
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