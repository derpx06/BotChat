package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.data.ChatMessage
import com.example.botchat.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
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
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                ambientColor = if (isDarkTheme) ElectricCyan.copy(alpha = 0.3f) else Purple40.copy(alpha = 0.3f)
            )
            .background(if (isDarkTheme) ChatInterfaceGradientDark else ChatInterfaceGradientLight)
            .border(
                width = 1.dp,
                brush = if (isDarkTheme) Brush.linearGradient(
                    listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
                ) else Brush.linearGradient(
                    listOf(Purple40.copy(alpha = 0.2f), Transparent)
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme)
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
private fun ChatMessageItem(message: ChatMessage, isDarkTheme: Boolean) {
    val isUserMessage = message.isUser
    val alignment = if (isUserMessage) Alignment.End else Alignment.Start
    val bubbleGradient = if (isUserMessage) {
        if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight
    } else {
        if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight
    }
    val bubbleShape = if (isUserMessage) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }
    val infiniteTransition = rememberInfiniteTransition(label = "Glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isUserMessage) 48.dp else 12.dp,
                    end = if (isUserMessage) 12.dp else 48.dp
                )
                .wrapContentWidth(alignment)
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleGradient)
                    .border(
                        width = 1.dp,
                        brush = if (isDarkTheme) Brush.linearGradient(
                            listOf(ElectricCyan.copy(alpha = glowAlpha), Transparent)
                        ) else Brush.linearGradient(
                            listOf(Purple40.copy(alpha = glowAlpha), Transparent)
                        ),
                        shape = bubbleShape
                    )
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    PureWhite.copy(alpha = 0.1f),
                                    Transparent
                                )
                            ),
                            alpha = 0.3f
                        )
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack
                    ),
                    modifier = Modifier
                        .then(if (!isUserMessage) Modifier.fillMaxWidth(0.8f) else Modifier.wrapContentSize())
                )
            }
        }
    }
}

@Composable
private fun ThinkingIndicator(isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight)
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