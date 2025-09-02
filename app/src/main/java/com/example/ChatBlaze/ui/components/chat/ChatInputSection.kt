package com.example.ChatBlaze.ui.components.chat

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ChatBlaze.ui.viewmodel.Chat.FileType
import com.example.ChatBlaze.ui.viewmodel.Chat.SelectedFile

private enum class ButtonState {
    Mic, Send, Loading, Recording
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    onMicClick: () -> Unit,
    isLoading: Boolean,
    isRecording: Boolean,
    selectedFiles: List<SelectedFile>,
    onAddFileClick: () -> Unit,
    onRemoveFile: (SelectedFile) -> Unit,
    isDarkTheme: Boolean = false,
    useGradientTheme: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val textFieldBackgroundColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF0F2F5)
    val iconColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF5F6368)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color(0xFF9E9E9E) else Color(0xFF8A8A8A)
    val sendButtonActiveColor = Color(0xFF2A9D8F)
    val micButtonColor = if (isDarkTheme) Color(0xFF48484A) else Color(0xFFDCDFE3)

    val buttonState by remember(inputText, selectedFiles, isLoading, isRecording) {
        derivedStateOf {
            when {
                isLoading -> ButtonState.Loading
                isRecording -> ButtonState.Recording
                inputText.isNotBlank() || selectedFiles.isNotEmpty() -> ButtonState.Send
                else -> ButtonState.Mic
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.background(
                if (isDarkTheme) Color.Black.copy(alpha = 0.3f)
                else Color.White.copy(alpha = 0.5f)
            )
        ) {
            AnimatedVisibility(visible = selectedFiles.isNotEmpty()) {
                FileThumbnails(
                    files = selectedFiles,
                    onRemoveFile = onRemoveFile,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    isDarkTheme = isDarkTheme
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 13.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onAddFileClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(textFieldBackgroundColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Attachment",
                        tint = iconColor
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(textFieldBackgroundColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputChange,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp)
                                .heightIn(min = 25.dp, max = 120.dp),
                            textStyle = TextStyle(
                                color = textColor,
                                fontSize = 17.sp
                            ),
                            cursorBrush = SolidColor(sendButtonActiveColor),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (inputText.isEmpty()) {
                                        Text(
                                            "Enter your message…",
                                            color = placeholderColor,
                                            fontSize = 17.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        AnimatedVisibility(
                            visible = inputText.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Text",
                                tint = iconColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onInputChange("") }
                                    )
                            )
                        }
                    }
                }

                val buttonBackgroundColor = when (buttonState) {
                    ButtonState.Send, ButtonState.Loading, ButtonState.Recording -> sendButtonActiveColor
                    ButtonState.Mic -> micButtonColor
                }

                IconButton(
                    onClick = {
                        when (buttonState) {
                            ButtonState.Mic, ButtonState.Recording -> onMicClick()
                            ButtonState.Send -> onSendClick()
                            ButtonState.Loading -> onStopClick()
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(buttonBackgroundColor)
                ) {
                    AnimatedContent(
                        targetState = buttonState,
                        transitionSpec = {
                            (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + scaleIn() with
                                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + scaleOut())
                                .using(SizeTransform(clip = false))
                        },
                        label = "SendButtonAnimation"
                    ) { state ->
                        when (state) {
                            ButtonState.Loading -> CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            ButtonState.Send -> Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.offset(x = (-2).dp)
                            )
                            ButtonState.Recording -> Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Stop Recording",
                                tint = Color.White
                            )
                            ButtonState.Mic -> Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileThumbnails(
    files: List<SelectedFile>,
    onRemoveFile: (SelectedFile) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        items(files) { file ->
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF0F2F5))
            ) {
                when (file.type) {
                    FileType.IMAGE -> {
                        AsyncImage(
                            model = file.uri,
                            contentDescription = "Selected photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    FileType.PDF -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PictureAsPdf,
                                contentDescription = "PDF File",
                                tint = if (isDarkTheme) Color.White else Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = file.name,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isDarkTheme) Color.LightGray else Color.DarkGray
                            )
                        }
                    }
                    else -> {}
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { onRemoveFile(file) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove file",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}


@Preview(name = "Chat Input - Mic State (Dark)")
@Composable
private fun ChatInputMicDarkPreview() {
    ChatInputSection(
        inputText = "",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        onMicClick = {},
        isLoading = false,
        isRecording = false,
        selectedFiles = emptyList(),
        onAddFileClick = {},
        onRemoveFile = {},
        isDarkTheme = true
    )
}

@Preview(name = "Chat Input - Recording State (Dark)")
@Composable
private fun ChatInputRecordingDarkPreview() {
    ChatInputSection(
        inputText = "",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        onMicClick = {},
        isLoading = false,
        isRecording = true,
        selectedFiles = emptyList(),
        onAddFileClick = {},
        onRemoveFile = {},
        isDarkTheme = true
    )
}

@Preview(name = "Chat Input - With Text (Dark)")
@Composable
private fun ChatInputTextDarkPreview() {
    ChatInputSection(
        inputText = "Hello World",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        onMicClick = {},
        isLoading = false,
        isRecording = false,
        selectedFiles = emptyList(),
        onAddFileClick = {},
        onRemoveFile = {},
        isDarkTheme = true
    )
}

@Preview(name = "Chat Input - Loading (Light)")
@Composable
private fun ChatInputLoadingLightPreview() {
    ChatInputSection(
        inputText = "Generating a story...",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        onMicClick = {},
        isLoading = true,
        isRecording = false,
        selectedFiles = emptyList(),
        onAddFileClick = {},
        onRemoveFile = {},
        isDarkTheme = false
    )
}

@Preview(name = "Chat Input - With Files (Light)")
@Composable
private fun ChatInputWithFilesLightPreview() {
    val files = remember {
        mutableStateListOf(
            SelectedFile(Uri.EMPTY, FileType.IMAGE, "vacation.jpg"),
            SelectedFile(Uri.EMPTY, FileType.PDF, "project-brief.pdf")
        )
    }
    ChatInputSection(
        inputText = "Here are the files",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        onMicClick = {},
        isLoading = false,
        isRecording = false,
        selectedFiles = files,
        onAddFileClick = {},
        onRemoveFile = { files.remove(it) },
        isDarkTheme = false
    )
}

