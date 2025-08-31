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
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.ChatBlaze.ui.viewmodel.Chat.FileType
import com.example.ChatBlaze.ui.viewmodel.Chat.SelectedFile

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    isLoading: Boolean,
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
    val sendButtonInactiveColor = if (isDarkTheme) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)
    val sendEnabled = (inputText.isNotBlank() || selectedFiles.isNotEmpty()) && !isLoading

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f))
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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                        tint = iconColor.copy(alpha = 1f)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(textFieldBackgroundColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
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
                                    .background(
                                        if (isDarkTheme) Color.Gray.copy(alpha = 0.3f) else Color.Gray.copy(
                                            alpha = 0.2f
                                        )
                                    )
                                    .padding(2.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onInputChange("") }
                                    )
                            )
                        }
                    }
                }
                IconButton(
                    onClick = if (isLoading) onStopClick else onSendClick,
                    enabled = sendEnabled || isLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                if (sendEnabled || isLoading)
                                    listOf(
                                        sendButtonActiveColor,
                                        sendButtonActiveColor.copy(alpha = 0.85f)
                                    )
                                else
                                    listOf(sendButtonInactiveColor, sendButtonInactiveColor)
                            )
                        )
                ) {
                    AnimatedContent(
                        targetState = Pair(isLoading, sendEnabled),
                        transitionSpec = {
                            (slideInVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) { it } + fadeIn() with
                                    slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) { -it } + fadeOut())
                                .using(SizeTransform(clip = false))
                        },
                        label = "SendButtonAnimation"
                    ) { (loading, enabled) ->
                        when {
                            loading -> CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            enabled -> Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.offset(x = (-2).dp)
                            )
                            else -> Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Send",
                                tint = if (isDarkTheme) Color(0xFF6E6E72) else Color(0xFFBDBDC2)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun ChatInputSectionPreview() {
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val selectedFiles = remember { mutableStateListOf<SelectedFile>() }

    ChatInputSection(
        inputText = inputText,
        onInputChange = { inputText = it },
        onSendClick = { isLoading = true },
        onStopClick = { isLoading = false },
        isLoading = isLoading,
        selectedFiles = selectedFiles,
        onAddFileClick = {
            selectedFiles.add(SelectedFile(Uri.EMPTY, FileType.IMAGE, "image.jpg"))
            selectedFiles.add(SelectedFile(Uri.EMPTY, FileType.PDF, "document.pdf"))
        },
        onRemoveFile = { selectedFiles.remove(it) },
        isDarkTheme = true
    )
}

@Preview
@Composable
fun FileThumbnailsPreview() {
    val files = listOf(
        SelectedFile(Uri.parse("https://example.com/image.jpg"), FileType.IMAGE, "Image.jpg"),
        SelectedFile(Uri.parse("https://example.com/document.pdf"), FileType.PDF, "Document.pdf")
    )
    FileThumbnails(files = files, onRemoveFile = {}, isDarkTheme = false)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        .background(Color.Black.copy(alpha = 0.5f))
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