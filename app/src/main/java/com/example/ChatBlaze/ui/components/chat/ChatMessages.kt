package com.example.ChatBlaze.ui.components.chat

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.database.ChatMessage
import com.example.ChatBlaze.ui.theme.*
import kotlinx.coroutines.launch

// Main composable for displaying the chat interface
@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    theme: String,
    streamingMessage: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    isOPH: Boolean,
    onPauseGeneration: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to the bottom when new messages or streaming content appears
    LaunchedEffect(messages.size, isLoading, streamingMessage.isNotEmpty()) {
        val totalItems = listState.layoutInfo.totalItemsCount
        if (totalItems > 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(totalItems - 1)
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
        when {
            !isOPH -> Text(
                text = "Chat hidden",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) GalacticGray else CoolGray,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
            messages.isEmpty() && !isLoading && streamingMessage.isEmpty() -> Text(
                text = "Start a conversation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) GalacticGray else CoolGray,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PaddingSmall),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(PaddingMedium),
                contentPadding = PaddingValues(bottom = PaddingExtraLarge)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(300),
                            placementSpec = tween(300)
                        )
                    )
                }
                if (isLoading || streamingMessage.isNotEmpty()) {
                    item(key = "model_response_item") {
                        ModelResponseItem(
                            content = streamingMessage,
                            isThinking = isLoading && streamingMessage.isEmpty(),
                            isDarkTheme = isDarkTheme,
                            theme = theme,
                            onPauseGeneration = onPauseGeneration,
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(300),
                                placementSpec = tween(300)
                            )
                        )
                    }
                }
            }
        }
    }
}

// Composable for individual chat messages
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isUser
    val alignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (isUserMessage) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 10.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 10.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isUserMessage) 0.dp else PaddingTiny,
                end = if (isUserMessage) PaddingTiny else 0.dp
            )
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = if (isUserMessage) maxWidth * 0.15f else 0.dp,
                    end = if (isUserMessage) 0.dp else maxWidth * 0.3f
                )
                .align(alignment)
                .clip(bubbleShape)
                .then(
                    when {
                        isUserMessage && theme == "gradient" -> Modifier.background(
                            brush = if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight,
                            alpha = 0.9f
                        )
                        isUserMessage -> Modifier.background(
                            color = if (isDarkTheme) AstralBlue.copy(alpha = 0.9f) else AccentIndigo.copy(alpha = 0.9f)
                        )
                        else -> Modifier.background(
                            brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
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
        ) {
            val annotatedText = if (!isUserMessage) parseResponse(message.content, isDarkTheme) else AnnotatedString(message.content)
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isUserMessage) 15.sp else 16.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack,
                    fontWeight = if (isUserMessage) FontWeight.Medium else FontWeight.Normal,
                    lineHeight = 28.sp,
                    letterSpacing = 0.4.sp
                )
            )
        }
    }
}

// Composable for model responses, including thinking and streaming states
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ModelResponseItem(
    content: String,
    isThinking: Boolean,
    isDarkTheme: Boolean,
    theme: String,
    onPauseGeneration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleShape = RoundedCornerShape(topStart = 10.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingExtraLarge),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(max =this.maxWidth * 0.7f)
                .clip(bubbleShape)
                .background(
                    brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                    alpha = 0.92f
                )
                .border(
                    width = 0.75.dp,
                    brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                    shape = bubbleShape
                )
                .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
        ) {
            if (isThinking) {
                TypingIndicator(isDarkTheme = isDarkTheme)

            } else {
                Text(
                    text = parseResponse(content, isDarkTheme),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 28.sp,
                        letterSpacing = 0.4.sp
                    )
                )
            }
            if (isThinking || content.isNotEmpty()) {
                IconButton(
                    onClick = onPauseGeneration,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isThinking) Icons.Default.Pause else Icons.Default.Stop,
                        contentDescription = "Pause/Stop generation",
                        tint = if (isDarkTheme) GalacticGray else CoolGray
                    )
                }
            }
        }
    }
}

// Animated typing indicator with three dots
@Composable
fun TypingIndicator(isDarkTheme: Boolean) {
    val baseDotColor = if (isDarkTheme) CoolGray.copy(alpha = 0.7f) else GalacticGray.copy(alpha = 0.5f)
    val activeDotColor = if (isDarkTheme) CoolGray.copy(alpha = 1f) else GalacticGray.copy(alpha = 0.8f)

    val dotSize = 7.dp
    val spacing = 5.dp
    val minScale = 0.6f
    val maxScale = 1.0f
    val animationDuration = 800
    val staggerDelay = 200

    @Composable
    fun WavingDot(animationDelay: Int) {
        val infiniteTransition = rememberInfiniteTransition(label = "WavingDotScale")
        val scale by infiniteTransition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    delayMillis = animationDelay,
                    easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dotScale"
        )
        val color by infiniteTransition.animateColor(
            initialValue = baseDotColor,
            targetValue = activeDotColor,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    delayMillis = animationDelay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dotColor"
        )
        Box(
            modifier = Modifier
                .size(dotSize)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(color = color, shape = CircleShape)
        )
    }

    Row(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        WavingDot(animationDelay = 0)
        WavingDot(animationDelay = staggerDelay)
        WavingDot(animationDelay = staggerDelay * 2)
    }
}

// Function to parse text for markdown-like formatting (headers, bold, italics)
private fun parseResponse(text: String, isDarkTheme: Boolean): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentIndex = 0

    text.split("\n").forEach { line ->
        val trimmedLine = line.trim()
        when {
            trimmedLine.startsWith("# ") -> {
                val headerText = trimmedLine.substring(2).trim() + "\n"
                builder.append(headerText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (isDarkTheme) ElectricCyan else SapphireBlue
                    ),
                    currentIndex,
                    currentIndex + headerText.length
                )
                currentIndex += headerText.length
            }
            trimmedLine.startsWith("## ") -> {
                val subheaderText = trimmedLine.substring(3).trim() + "\n"
                builder.append(subheaderText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = if (isDarkTheme) GalacticGray else CoolGray
                    ),
                    currentIndex,
                    currentIndex + subheaderText.length
                )
                currentIndex += subheaderText.length
            }
            else -> {
                var lineText = trimmedLine
                var lastIndex = 0
                val outputLine = buildString {
                    val boldRegex = Regex("\\*\\*([^\\*\\*]+?)\\*\\*")
                    boldRegex.findAll(lineText).forEach { match ->
                        append(lineText.substring(lastIndex, match.range.first))
                        val boldText = match.groupValues[1]
                        builder.append(boldText)
                        builder.addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            currentIndex,
                            currentIndex + boldText.length
                        )
                        currentIndex += boldText.length
                        lastIndex = match.range.last + 1
                    }
                    append(lineText.substring(lastIndex))
                    lineText = toString()
                    lastIndex = 0
                    clear()
                    val italicRegex = Regex("_([^_]+?)_")
                    italicRegex.findAll(lineText).forEach { match ->
                        append(lineText.substring(lastIndex, match.range.first))
                        val italicText = match.groupValues[1]
                        builder.append(italicText)
                        builder.addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            currentIndex,
                            currentIndex + italicText.length
                        )
                        currentIndex += italicText.length
                        lastIndex = match.range.last + 1
                    }
                    append(lineText.substring(lastIndex))
                }
                val finalText = outputLine + "\n"
                builder.append(finalText)
                currentIndex += finalText.length
            }
        }
    }
    return builder.toAnnotatedString()
}

// Padding constants used throughout the UI
private val PaddingTiny = 4.dp
internal val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp

// --- Previews for All Components ---

@Preview(name = "Chat Message Item - User - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ChatMessageItemUserLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessageItem(
            message = ChatMessage(id = 1, content = "Hello, this is a user message.", isUser = true, timestamp = System.currentTimeMillis(), sessionId = 1L),
            isDarkTheme = false,
            theme = "default"
        )
    }
}

@Preview(name = "Chat Message Item - User - Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ChatMessageItemUserDarkPreview() {
    BotChatTheme(darkTheme = true) {
        ChatMessageItem(
            message = ChatMessage(id = 1, content = "Hello, this is a user message in dark mode.", isUser = true, timestamp = System.currentTimeMillis(), sessionId = 1L),
            isDarkTheme = true,
            theme = "default"
        )
    }
}

@Preview(name = "Chat Message Item - Model - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ChatMessageItemModelLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessageItem(
            message = ChatMessage(id = 2, content = "Hi there! I'm the model.\n# Header\n**Bold** and _italic_.", isUser = false, timestamp = System.currentTimeMillis(), sessionId = 1L),
            isDarkTheme = false,
            theme = "default"
        )
    }
}

@Preview(name = "Chat Message Item - Model - Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ChatMessageItemModelDarkPreview() {
    BotChatTheme(darkTheme = true) {
        ChatMessageItem(
            message = ChatMessage(id = 2, content = "Hi there! I'm the model in dark mode.", isUser = false, timestamp = System.currentTimeMillis(), sessionId = 1L),
            isDarkTheme = true,
            theme = "default"
        )
    }
}

@Preview(name = "Model Response Item - Thinking - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ModelResponseItemThinkingLightPreview() {
    BotChatTheme(darkTheme = false) {
        ModelResponseItem(
            content = "",
            isThinking = true,
            isDarkTheme = false,
            theme = "default",
            onPauseGeneration = {}
        )
    }
}

@Preview(name = "Model Response Item - Thinking - Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ModelResponseItemThinkingDarkPreview() {
    BotChatTheme(darkTheme = true) {
        ModelResponseItem(
            content = "",
            isThinking = true,
            isDarkTheme = true,
            theme = "default",
            onPauseGeneration = {}
        )
    }
}

@Preview(name = "Model Response Item - Streaming - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ModelResponseItemStreamingLightPreview() {
    BotChatTheme(darkTheme = false) {
        ModelResponseItem(
            content = "This is a streamed response...",
            isThinking = false,
            isDarkTheme = false,
            theme = "default",
            onPauseGeneration = {}
        )
    }
}

@Preview(name = "Typing Indicator - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TypingIndicatorLightPreview() {
    BotChatTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            TypingIndicator(isDarkTheme = false)
        }
    }
}

@Preview(name = "Typing Indicator - Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TypingIndicatorDarkPreview() {
    BotChatTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            TypingIndicator(isDarkTheme = true)
        }
    }
}

@Preview(name = "Chat Messages - Empty - Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatMessagesEmptyLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessages(
            messages = emptyList(),
            isLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false,
            isOPH = true
        )
    }
}

@Preview(name = "Chat Messages - Hidden - Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatMessagesHiddenDarkPreview() {
    BotChatTheme(darkTheme = true) {
        ChatMessages(
            messages = emptyList(),
            isLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = true,
            isOPH = false
        )
    }
}

@Preview(name = "Chat Messages - With Content - Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatMessagesWithContentLightPreview() {
    BotChatTheme(darkTheme = false) {
        val sampleMessages = listOf(
            ChatMessage(id = 1, content = "Hello!", isUser = true, timestamp = System.currentTimeMillis() - 2000, sessionId = 1L),
            ChatMessage(id = 2, content = "Hi there!", isUser = false, timestamp = System.currentTimeMillis() - 1000, sessionId = 1L)
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false,
            isOPH = true
        )
    }
}

@Preview(name = "Chat Messages - Loading - Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatMessagesLoadingDarkPreview() {
    BotChatTheme(darkTheme = true) {
        val sampleMessages = listOf(
            ChatMessage(id = 1, content = "Question?", isUser = true, timestamp = System.currentTimeMillis() - 1000, sessionId = 1L)
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = true,
            theme = "gradient",
            streamingMessage = "",
            isDarkTheme = true,
            isOPH = true
        )
    }
}

@Preview(name = "Chat Messages - Streaming - Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatMessagesStreamingDarkPreview() {
    BotChatTheme(darkTheme = true) {
        val sampleMessages = listOf(
            ChatMessage(id = 1, content = "What is Kotlin?", isUser = true, timestamp = System.currentTimeMillis() - 1000, sessionId = 1L)
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = false,
            theme = "default",
            streamingMessage = "Kotlin is a programming language...",
            isDarkTheme = true,
            isOPH = true
        )
    }
}

@Preview(name = "Markdown Parsing - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MarkdownParsingPreview() {
    BotChatTheme(darkTheme = false) {
        val markdownText = "# Header\nThis is **bold** and _italic_."
        Box(Modifier.padding(16.dp)) {
            Text(parseResponse(markdownText, isDarkTheme = false))
        }
    }
}