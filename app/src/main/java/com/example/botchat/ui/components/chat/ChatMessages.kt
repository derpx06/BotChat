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

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(
            animationSpec = spring(dampingRatio = 0.7f)
        ),
        exit = fadeOut(animationSpec = tween(300))
    ) {
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
                verticalArrangement = Arrangement.spacedBy(PaddingMedium)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message = message, isDarkTheme = isDarkTheme, theme = theme)
                }
                item {
                    AnimatedVisibility(
                        visible = isLoading,
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
private fun ChatMessageItem(message: ChatMessage, isDarkTheme: Boolean, theme: String) {
    val isUserMessage = message.isUser
    val alignment = if (isUserMessage) Alignment.End else Alignment.Start
    val bubbleShape = if (isUserMessage) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 10.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 10.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
            initialOffsetX = { if (isUserMessage) it else -it },
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
        ),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
            targetOffsetX = { if (isUserMessage) it else -it },
            animationSpec = tween(300)
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
                            )
                            isUserMessage && theme == "mixed" -> Modifier.background(
                                brush = if (isDarkTheme) CardGradientDark else CardGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "cosmic" -> Modifier.background(
                                brush = if (isDarkTheme) CosmicBubbleGradientDark else CosmicBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "pastel" -> Modifier.background(
                                brush = if (isDarkTheme) PastelBubbleGradientDark else PastelBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "metallic" -> Modifier.background(
                                brush = if (isDarkTheme) MetallicBubbleGradientDark else MetallicBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "jewel" -> Modifier.background(
                                brush = if (isDarkTheme) JewelBubbleGradientDark else JewelBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "minimal" -> Modifier.background(
                                brush = if (isDarkTheme) MinimalBubbleGradientDark else MinimalBubbleGradientLight,
                                shape = bubbleShape,
                                alpha = 0.9f
                            )
                            isUserMessage && theme == "plain" -> Modifier.background(
                                color = if (isDarkTheme) AstralBlue.copy(alpha = 0.9f) else AccentIndigo.copy(alpha = 0.9f),
                                shape = bubbleShape
                            )
                            !isUserMessage && theme == "gradient" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "mixed" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "cosmic" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "pastel" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "metallic" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "jewel" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            !isUserMessage && theme == "minimal" -> Modifier.background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                shape = bubbleShape,
                                alpha = 0.92f
                            )
                            else -> Modifier.background(
                                color = if (isDarkTheme) CharredBlack.copy(alpha = 0.92f) else CloudWhite.copy(alpha = 0.92f),
                                shape = bubbleShape
                            )
                        }
                    )
                    .border(
                        width = 0.75.dp,
                        brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                        shape = bubbleShape
                    )
                    .padding(
                        horizontal = PaddingLarge,
                        vertical = PaddingMedium
                    )
                    .widthIn(max = if (isUserMessage) 300.dp else 400.dp)
                    .scale(if (isHovered) 1.02f else 1f)
                    .animateContentSize(animationSpec = spring(dampingRatio = 0.8f))
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
                        lineHeight = 30.sp,
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
            .scale(scale)
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
                lineHeight = 20.sp,
                letterSpacing = 0.3.sp
            )
        )
    }
}

// Padding Constants
private val PaddingTiny = 4.dp
internal val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp