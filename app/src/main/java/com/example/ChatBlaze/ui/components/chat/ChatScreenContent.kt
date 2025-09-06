package com.example.ChatBlaze.ui.components.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ChatBlaze.ui.theme.*
import com.example.ChatBlaze.ui.viewmodel.Chat.ChatViewModel
import com.example.ChatBlaze.ui.viewmodel.Chat.FileType
import com.example.ChatBlaze.ui.viewmodel.Chat.SelectedFile
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel

private val PaddingLarge = 16.dp

@Composable
fun ChatScreenContent(
    chatViewModel: ChatViewModel,
    settingViewModel: SettingViewModel,
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isModelLoading: Boolean
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val selectedFiles by chatViewModel.selectedFiles.collectAsStateWithLifecycle()
    val isRecording by chatViewModel.isRecording.collectAsStateWithLifecycle()
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val selectedTheme by settingViewModel.theme.collectAsStateWithLifecycle(initialValue = "gradient")

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            chatViewModel.startSpeechToText()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris: List<Uri> ->
            val selectedFilesList = uris.mapNotNull { uri ->
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)

                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val fileName = it.getString(nameIndex)
                        val mimeType = context.contentResolver.getType(uri)

                        val fileType = when {
                            mimeType?.startsWith("image/") == true -> FileType.IMAGE
                            mimeType == "application/pdf" -> FileType.PDF
                            else -> FileType.OTHER
                        }
                        return@mapNotNull SelectedFile(uri, fileType, fileName)
                    }
                }
                null
            }
            chatViewModel.addFiles(selectedFilesList)
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = if (isDarkTheme) BackgroundGradientDark else BackgroundGradientLight,
                alpha = 0.9f
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TopBar(
                onMenuClick = onDrawerClicked,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
            ChatMessages(
                messages = uiState.messages,
                isLoading = uiState.isLoading,
                theme = selectedTheme,
                streamingMessage = uiState.streamingMessage,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                isModelLoading = isModelLoading
            )
        }

        ChatInputSection(
            inputText = uiState.inputText,
            onInputChange = chatViewModel::updateInputText,
            onSendClick = chatViewModel::sendMessage,
            onStopClick = chatViewModel::cancelProcessing,
            onMicClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    chatViewModel.startSpeechToText()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            isLoading = uiState.isLoading,
            isRecording = isRecording,
            isDarkTheme = isDarkTheme,
            useGradientTheme = selectedTheme == "gradient",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = PaddingLarge, vertical = PaddingLarge),
            selectedFiles = selectedFiles,
            onAddFileClick = {
                filePickerLauncher.launch(arrayOf("image/*", "application/pdf"))
            },
            onRemoveFile = chatViewModel::removeFile
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
                            color = if (isDarkTheme) MidnightBlack.copy(alpha = 0.95f) else CloudWhite.copy(
                                alpha = 0.95f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                )
            }
        }
    }
}
