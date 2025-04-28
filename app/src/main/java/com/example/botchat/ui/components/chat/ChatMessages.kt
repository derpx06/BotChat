package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.Data.ChatMessage
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
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDarkTheme) ChatInterfaceGradientDark else ChatInterfaceGradientLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            state = listState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme)
            }
            item {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ThinkingIndicator(isDarkTheme = isDarkTheme)
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage, isDarkTheme: Boolean) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleGradient = if (message.isUser) {
        if (isDarkTheme) SendButtonGradientDark else SendButtonGradientLight
    } else {
        if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isUser) 64.dp else 16.dp,
                end = if (message.isUser) 16.dp else 64.dp
            )
            .wrapContentWidth(alignment)
    ) {
        Box(
            modifier = Modifier
                .background(bubbleGradient, RoundedCornerShape(14.dp))
                .padding(if (message.isUser) 8.dp else 10.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = when (message.content.length) {
                        in 0..50 -> 14.sp
                        in 51..100 -> 13.sp
                        else -> 12.sp
                    },
                    color = if (isDarkTheme) StarlightWhite else Black
                )
            )
        }
    }
}

@Composable
private fun ThinkingIndicator(isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
            .background(if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight, RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            color = if (isDarkTheme) NeonCyan else Black,
            strokeWidth = 1.5.dp
        )
        Text(
            text = "AI is thinking...",
            style = MaterialTheme.typography.bodySmall.copy(color = if (isDarkTheme) StarlightWhite else Black)
        )
    }
}