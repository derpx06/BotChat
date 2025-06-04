package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.database.ChatMessage
import com.example.botchat.ui.theme.*
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    theme: String,
    streamingMessage: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isOPH: Boolean
) {
    LaunchedEffect(messages, streamingMessage, isOPH) {
        Log.d("ChatMessages", "Messages: ${messages.size}, Contents: ${messages.map { it.content }}, Streaming: $streamingMessage, isOPH: $isOPH")
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages, streamingMessage) {
        coroutineScope.launch {
            val targetIndex = if (messages.isNotEmpty() || streamingMessage.isNotEmpty()) {
                messages.size + (if (streamingMessage.isNotEmpty()) 1 else 0)
            } else {
                0
            }
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                when (theme) {
                    "gradient" -> Modifier.background(
                        brush = if (isDarkTheme) ChatInterfaceGradientDark else ChatInterfaceGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    else -> Modifier.background(
                        color = if (isDarkTheme) MidnightBlack else CloudWhite,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                }
            )
            .border(
                width = 0.5.dp,
                brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(PaddingLarge)
    ) {
        if (!isOPH) {
            Text(
                text = "Chat hidden",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) GalacticGray else CoolGray,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (messages.isEmpty() && streamingMessage.isEmpty() && !isLoading) {
            Text(
                text = "Start a conversation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) GalacticGray else CoolGray,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PaddingSmall),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(PaddingMedium)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        modifier = Modifier.animateItem(fadeInSpec = tween(200))
                    )
                }
                if (streamingMessage.isNotEmpty()) {
                    item(key = "streaming_${streamingMessage.hashCode()}") {
                        StreamingMessageItem(
                            content = streamingMessage,
                            isDarkTheme = isDarkTheme,
                            theme = theme,
                            modifier = Modifier
                                .padding(bottom = PaddingLarge)
                                .animateItem(fadeInSpec = tween(200))
                        )
                    }
                }
                item(key = "loading") {
                    AnimatedVisibility(
                        visible = isLoading && streamingMessage.isEmpty(),
                        enter = fadeIn(animationSpec = tween(350)) + slideInVertically(),
                        exit = fadeOut(animationSpec = tween(350)) + slideOutVertically()
                    ) {
                        ThinkingIndicator(isDarkTheme = isDarkTheme)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isUser
    val alignment = if (isUserMessage) Alignment.End else Alignment.Start
    val bubbleShape = if (isUserMessage) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 10.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 10.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isUserMessage) PaddingExtraLarge else PaddingTiny,
                end = if (isUserMessage) PaddingTiny else PaddingExtraLarge,
                top = PaddingSmall,
                bottom = PaddingSmall
            )
            .wrapContentWidth(alignment)
    ) {
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .then(
                    when {
                        isUserMessage && theme == "gradient" -> Modifier.background(
                            brush = if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight,
                            shape = bubbleShape,
                            alpha = 0.9f
                        )
                        isUserMessage -> Modifier.background(
                            color = if (isDarkTheme) AstralBlue.copy(alpha = 0.9f) else AccentIndigo.copy(alpha = 0.9f),
                            shape = bubbleShape
                        )
                        else -> Modifier.background(
                            brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                            shape = bubbleShape,
                            alpha = 0.92f
                        )
                    }
                )
                .border(
                    width = 0.75.dp,
                    brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                    shape = bubbleShape
                )
                .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
                .widthIn(max = if (isUserMessage) 300.dp else 400.dp)
                .scale(if (isHovered) 1.02f else 1f)
        ) {
            val annotatedText = if (!isUserMessage) parseResponse(message.content) else AnnotatedString(message.content)
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isUserMessage) 15.sp else 16.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack,
                    fontWeight = if (isUserMessage) FontWeight.Medium else FontWeight.Normal,
                    lineHeight = 30.sp,
                    letterSpacing = 0.4.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StreamingMessageItem(
    content: String,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    val bubbleShape = RoundedCornerShape(topStart = 10.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = PaddingTiny, end = PaddingExtraLarge, top = PaddingSmall, bottom = PaddingSmall)
            .wrapContentWidth(Alignment.Start)
    ) {
        Row(
            modifier = Modifier
                .clip(bubbleShape)
                .background(
                    brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                    shape = bubbleShape,
                    alpha = 0.92f
                )
                .border(
                    width = 0.75.dp,
                    brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                    shape = bubbleShape
                )
                .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
                .widthIn(max = 400.dp)
                .animateContentSize(animationSpec = spring(dampingRatio = 0.8f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parseResponse(content),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 30.sp,
                    letterSpacing = 0.4.sp
                ),
                modifier = Modifier.weight(1f)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = PaddingSmall),
                color = if (isDarkTheme) NeonBlue else Aquamarine,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun parseResponse(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentIndex = 0
    text.split("\n").forEach { line ->
        val trimmedLine = line.trim()
        when {
            trimmedLine.startsWith("# ") -> {
                val titleText = trimmedLine.substring(2).trim() + "\n"
                builder.append(titleText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (isSystemInDarkTheme()) ElectricCyan else SapphireBlue
                    ),
                    currentIndex,
                    currentIndex + titleText.length
                )
                currentIndex += titleText.length
            }
            trimmedLine.startsWith("## ") -> {
                val subtitleText = trimmedLine.substring(3).trim() + "\n"
                builder.append(subtitleText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = if (isSystemInDarkTheme()) GalacticGray else CoolGray
                    ),
                    currentIndex,
                    currentIndex + subtitleText.length
                )
                currentIndex += subtitleText.length
            }
            else -> {
                val normalText = line + "\n"
                builder.append(normalText)
                currentIndex += normalText.length
            }
        }
    }
    return builder.toAnnotatedString()
}

@Composable
fun ThinkingIndicator(isDarkTheme: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingIndicator")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
            .padding(start = PaddingTiny)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                alpha = 0.8f
            )
            .border(
                width = 0.5.dp,
                brush = if (isDarkTheme) Brush.linearGradient(listOf(NeonBlue, GalacticGray)) else Brush.linearGradient(listOf(Aquamarine, CoolGray)),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = PaddingMedium, vertical = PaddingSmall)
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = if (isDarkTheme) NeonBlue else Aquamarine,
            strokeWidth = 2.5.dp
        )
        Text(
            text = "AI is processing...",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                color = if (isDarkTheme) GalacticGray else CoolGray,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                letterSpacing = 0.3.sp
            )
        )
    }
}

private val PaddingTiny = 4.dp
internal val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp