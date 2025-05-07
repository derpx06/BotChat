package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.database.ChatMessage
import com.example.botchat.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    theme: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .then(
                when (theme) {
                    "gradient", "mixed" -> Modifier.background(
                        brush = if (isDarkTheme) ChatInterfaceGradientDark else ChatInterfaceGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 1.0f
                    )
                    else -> Modifier.background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDarkTheme) 0.15f else 0.5f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                }
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme, theme = theme)
            }
            item {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                ) {
                    ThinkingIndicator(isDarkTheme = isDarkTheme)
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage, isDarkTheme: Boolean, theme: String) {
    val isUserMessage = message.isUser
    val alignment = if (isUserMessage) Alignment.End else Alignment.Start
    val bubbleShape = if (isUserMessage) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isUserMessage) 48.dp else 8.dp,
                    end = if (isUserMessage) 8.dp else 8.dp
                )
                .wrapContentWidth(alignment)
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .then(
                        when {
                            isUserMessage && theme != "plain" -> Modifier.background(
                                brush = if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 1.0f
                            )
                            !isUserMessage && theme != "plain" -> Modifier.background(
                                brush = if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight,
                                shape = bubbleShape,
                                alpha = 1.0f
                            )
                            isUserMessage && theme == "plain" -> Modifier.background(
                                color = if (isDarkTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Purple40.copy(alpha = 0.2f),
                                shape = bubbleShape
                            )
                            else -> Modifier.background(
                                color = if (isDarkTheme) MaterialTheme.colorScheme.surface.copy(alpha = 0.3f) else MistGray.copy(alpha = 0.3f),
                                shape = bubbleShape
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = bubbleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 300.dp)
            ) {
                val annotatedText = if (!isUserMessage) {
                    parseResponse(message.content)
                } else {
                    AnnotatedString(message.content)
                }
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun parseResponse(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentIndex = 0
    text.split("\n").forEach { line ->
        val trimmedLine = line.trim()
        if (trimmedLine.startsWith("# ")) {
            val titleText = trimmedLine.substring(2).trim() + "\n"
            builder.append(titleText)
            builder.addStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                currentIndex,
                currentIndex + titleText.length
            )
            currentIndex += titleText.length
        } else if (trimmedLine.startsWith("## ")) {
            val subtitleText = trimmedLine.substring(3).trim() + "\n"
            builder.append(subtitleText)
            builder.addStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                currentIndex,
                currentIndex + subtitleText.length
            )
            currentIndex += subtitleText.length
        } else {
            val normalText = line + "\n"
            builder.append(normalText)
            currentIndex += normalText.length
        }
    }
    return builder.toAnnotatedString()
}
@Composable
fun ThinkingIndicator(isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = if (isDarkTheme) ElectricCyan else Purple40,
            strokeWidth = 2.dp
        )
        Text(
            text = "AI is thinking...",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                color = if (isDarkTheme) PureWhite else SlateBlack
            )
        )
    }
}