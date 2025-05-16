package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.database.ChatMessage
import com.example.botchat.ui.theme.*
import kotlinx.coroutines.launch

// Padding Constants
private val PaddingTiny = 4.dp
private val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp

@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    theme: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
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
                    "gradient" -> Modifier.background(
                        brush = if (isDarkTheme) ChatInterfaceGradientDark else ChatInterfaceGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "mixed" -> Modifier.background(
                        brush = if (isDarkTheme) SleekGradientDark else SleekGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "cosmic" -> Modifier.background(
                        brush = if (isDarkTheme) CosmicGradientDark else CosmicGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "pastel" -> Modifier.background(
                        brush = if (isDarkTheme) PastelGradientDark else PastelGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "metallic" -> Modifier.background(
                        brush = if (isDarkTheme) MetallicGradientDark else MetallicGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "jewel" -> Modifier.background(
                        brush = if (isDarkTheme) JewelGradientDark else JewelGradientLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        alpha = 0.95f
                    )
                    "minimal" -> Modifier.background(
                        brush = if (isDarkTheme) MinimalGradientDark else MinimalGradientLight,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingSmall),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(PaddingMedium),
            contentPadding = PaddingValues(vertical = PaddingLarge)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme, theme = theme)
            }
            item {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(400)) + slideInVertically(),
                    exit = fadeOut(animationSpec = tween(400)) + slideOutVertically()
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
        RoundedCornerShape(topStart = 16.dp, topEnd = 8.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 8.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    var isHovered by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
            initialOffsetX = { if (isUserMessage) it else -it },
            animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f)
        ),
        exit = fadeOut(animationSpec = tween(400)) + slideOutHorizontally(
            targetOffsetX = { if (isUserMessage) it else -it },
            animationSpec = tween(400)
        )
    ) {
        Box(
            modifier = Modifier
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
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "mixed" -> Modifier.background(
                                brush = if (isDarkTheme) CardGradientDark else CardGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "cosmic" -> Modifier.background(
                                brush = if (isDarkTheme) CosmicBubbleGradientDark else CosmicBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "pastel" -> Modifier.background(
                                brush = if (isDarkTheme) PastelBubbleGradientDark else PastelBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "metallic" -> Modifier.background(
                                brush = if (isDarkTheme) MetallicBubbleGradientDark else MetallicBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "jewel" -> Modifier.background(
                                brush = if (isDarkTheme) JewelBubbleGradientDark else JewelBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "minimal" -> Modifier.background(
                                brush = if (isDarkTheme) MinimalBubbleGradientDark else MinimalBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            isUserMessage && theme == "plain" -> Modifier.background(
                                color = if (isDarkTheme) AstralBlue.copy(alpha = 0.9f) else AccentIndigo.copy(alpha = 0.9f),
                                shape = bubbleShape
                            ).shadow(4.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "gradient" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "mixed" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "cosmic" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "pastel" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "metallic" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "jewel" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            !isUserMessage && theme == "minimal" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                            else -> Modifier.background(
                                color = if (isDarkTheme) CharredBlack.copy(alpha = 0.92f) else CloudWhite.copy(alpha = 0.92f),
                                shape = bubbleShape
                            ).shadow(2.dp, shape = bubbleShape, ambientColor = if (isHovered) NeonBlue else Transparent)
                        }
                    )
                    .border(
                        width = 0.5.dp,
                        brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                        shape = bubbleShape
                    )
                    .padding(
                        horizontal = PaddingLarge,
                        vertical = PaddingMedium
                    )
                    .widthIn(max = if (isUserMessage) 300.dp else 400.dp)
                    .animateContentSize(animationSpec = spring(dampingRatio = 0.9f))
            ) {
                val annotatedText = if (!isUserMessage) {
                    parseResponse(message.content)
                } else {
                    AnnotatedString(message.content)
                }
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isUserMessage) 15.sp else 16.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontWeight = if (isUserMessage) FontWeight.Medium else FontWeight.Normal,
                        lineHeight = 28.sp,
                        letterSpacing = 0.4.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
            .padding(horizontal = PaddingMedium, vertical = PaddingSmall)
            .animateContentSize(animationSpec = tween(300)),
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
                lineHeight = 20.sp
            )
        )
    }
}