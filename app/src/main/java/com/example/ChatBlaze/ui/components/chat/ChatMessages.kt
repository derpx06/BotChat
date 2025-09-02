package com.example.ChatBlaze.ui.components.chat

import BotChatTheme
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ChatBlaze.data.database.ChatMessage
import com.example.ChatBlaze.ui.theme.AccentIndigo
import com.example.ChatBlaze.ui.theme.AstralBlue
import com.example.ChatBlaze.ui.theme.BackgroundGradientDark
import com.example.ChatBlaze.ui.theme.BottomFadeGradientDark
import com.example.ChatBlaze.ui.theme.BottomFadeGradientLight
import com.example.ChatBlaze.ui.theme.ChatBubbleGradientDark
import com.example.ChatBlaze.ui.theme.ChatBubbleGradientLight
import com.example.ChatBlaze.ui.theme.ChatInterfaceGradientLight
import com.example.ChatBlaze.ui.theme.CloudWhite
import com.example.ChatBlaze.ui.theme.CoolGray
import com.example.ChatBlaze.ui.theme.GalacticGray
import com.example.ChatBlaze.ui.theme.MidnightBlack
import com.example.ChatBlaze.ui.theme.OnyxBlack
import com.example.ChatBlaze.ui.theme.PureWhite
import com.example.ChatBlaze.ui.theme.ResponseGradientDarkMode
import com.example.ChatBlaze.ui.theme.ResponseGradientLightMode
import com.example.ChatBlaze.ui.theme.SlateBlack
import com.example.ChatBlaze.ui.theme.TopBarUnderlineDark
import com.example.ChatBlaze.ui.theme.TopBarUnderlineLight
import com.example.ChatBlaze.ui.theme.WhiteTranslucent
import kotlinx.coroutines.launch

private sealed class ParsedContent {
    data class TextContent(val text: AnnotatedString) : ParsedContent()
    data class CodeContent(val language: String, val code: String) : ParsedContent()
}
val PaddingTiny = 4.dp
val PaddingSmall = 8.dp
val PaddingMedium = 12.dp
@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    isModelLoading: Boolean,
    theme: String,
    streamingMessage: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onPauseGeneration: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                when (theme) {
                    "gradient" -> Modifier.background(
                        brush = if (isDarkTheme) BackgroundGradientDark else ChatInterfaceGradientLight,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                    )
                    else -> Modifier.background(
                        color = if (isDarkTheme) MidnightBlack else CloudWhite,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
                }
            )
            .border(
                width = 0.3.dp,
                brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            )
    ) {
        if (messages.isEmpty() && !isLoading && !isModelLoading && streamingMessage.isEmpty()) {
            EmptyChatPlaceholder(isDarkTheme = isDarkTheme)
        } else {
            ChatList(
                messages = messages,
                isLoading = isLoading,
                isModelLoading = isModelLoading,
                streamingMessage = streamingMessage,
                isDarkTheme = isDarkTheme,
                theme = theme,
                onPauseGeneration = onPauseGeneration
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatList(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    isModelLoading: Boolean,
    streamingMessage: String,
    isDarkTheme: Boolean,
    theme: String,
    onPauseGeneration: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToBottomButton by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index < listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(messages.size, streamingMessage.length) {
        if (!showScrollToBottomButton) {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems > 0) {
                coroutineScope.launch {
                    listState.animateScrollToItem(totalItems - 1)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingSmall),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(PaddingSmall),
            contentPadding = PaddingValues(bottom = paddingUltraLarge, top = PaddingMedium)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    isDarkTheme = isDarkTheme,
                    theme = theme,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                )
            }
            if (isModelLoading) {
                item(key = "model_loading_indicator") {
                    ModelLoadingIndicator(isDarkTheme = isDarkTheme)
                }
            }
            if (isLoading || streamingMessage.isNotEmpty()) {
                item(key = "model_response_item") {
                    ModelResponseItem(
                        content = streamingMessage,
                        isThinking = isLoading && streamingMessage.isEmpty(),
                        isDarkTheme = isDarkTheme,
                        onPauseGeneration = onPauseGeneration
                    )
                }
            }
        }

        ScrollToBottomButton(
            visible = showScrollToBottomButton,
            isDarkTheme = isDarkTheme,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
                }
            }
        )
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (message.isUser) {
            UserMessageBubble(message, isDarkTheme, theme)
        } else {
            ModelMessageBubble(message, isDarkTheme)
        }
    }
}

@Composable
private fun UserMessageBubble(message: ChatMessage, isDarkTheme: Boolean, theme: String) {
    val bubbleShape = RoundedCornerShape(topStart = 24.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(bubbleShape)
            .then(
                if (theme == "gradient") Modifier.background(
                    brush = if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight
                )
                else Modifier.background(
                    color = if (isDarkTheme) AstralBlue else AccentIndigo
                )
            )
            .border(
                width = 0.75.dp,
                brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                shape = bubbleShape
            )
            .padding(PaddingSmall)
    ) {
        Column {
            if (message.attachmentUris.isNotEmpty()) {
                AttachmentImages(
                    uris = message.attachmentUris,
                    modifier = Modifier.padding(bottom = if (message.content.isNotBlank()) PaddingSmall else 0.dp)
                )
            }
            if (message.content.isNotBlank()) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(horizontal = PaddingMedium - PaddingTiny, vertical = PaddingSmall)
                )
            }
        }
    }
}

@Composable
private fun ModelMessageBubble(message: ChatMessage, isDarkTheme: Boolean) {
    var isThinkingVisible by remember { mutableStateOf(false) }
    val (thinkingText, responseText) = remember(message.content) {
        parseThoughtAndResponse(message.content)
    }
    val bubbleShape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(bubbleShape)
            .background(
                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
            )
            .border(
                width = 0.55.dp,
                brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                shape = bubbleShape
            )
            .padding(horizontal = PaddingLarge, vertical = PaddingLarge)
    ) {
        Column {
            if (thinkingText != null) {
                ThinkingBlock(
                    thinkingText = thinkingText,
                    isThinkingVisible = isThinkingVisible,
                    isDarkTheme = isDarkTheme,
                    onToggleVisibility = { isThinkingVisible = !isThinkingVisible }
                )
            }
            MessageContent(responseText = responseText, isDarkTheme = isDarkTheme)
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ModelResponseItem(
    content: String,
    isThinking: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    onPauseGeneration: () -> Unit
) {
    val (thinkingText, responseText) = remember(content) {
        parseThoughtAndResponse(content)
    }
    var isThinkingVisible by remember { mutableStateOf(false) }
    val bubbleShape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(bubbleShape)
                .background(
                    brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                )
                .border(
                    width = 0.55.dp,
                    brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                    shape = bubbleShape
                )
                .padding(horizontal = PaddingLarge, vertical = PaddingLarge)
        ) {
            Column {
                if (isThinking) {
                    TypingIndicator(isDarkTheme = isDarkTheme)
                } else {
                    if (thinkingText != null) {
                        ThinkingBlock(
                            thinkingText = thinkingText,
                            isThinkingVisible = isThinkingVisible,
                            isDarkTheme = isDarkTheme,
                            onToggleVisibility = { isThinkingVisible = !isThinkingVisible }
                        )
                    }
                    MessageContent(responseText = responseText, isDarkTheme = isDarkTheme)
                }
            }
        }
    }
}

@Composable
private fun EmptyChatPlaceholder(isDarkTheme: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingExtraLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingLarge)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = (if (isDarkTheme) GalacticGray else OnyxBlack).copy(alpha = 0.8f)
            )
            Text(
                text = "Hey there! Let's get started. 😊\nWhat's on your mind?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isDarkTheme) GalacticGray else OnyxBlack,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            )
        }
    }
}

@Composable
private fun BoxScope.ScrollToBottomButton(
    visible: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = paddingUltraLarge + PaddingSmall),
        enter = fadeIn(animationSpec = tween(200, 100)) + slideInVertically(
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
        ) { it / 2 },
        exit = slideOutVertically(
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
        ) { it / 2 } + fadeOut(animationSpec = tween(150))
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = (if (isDarkTheme) OnyxBlack else CloudWhite).copy(alpha = 0.9f),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                contentDescription = "Scroll to Bottom",
                tint = if (isDarkTheme) PureWhite else SlateBlack,
                modifier = Modifier.padding(PaddingSmall)
            )
        }
    }
}

@Composable
internal fun TypingIndicator(
    isDarkTheme: Boolean,
    barCount: Int = 5
) {
    val baseColor = if (isDarkTheme) CoolGray.copy(alpha = 0.5f) else GalacticGray.copy(alpha = 0.5f)
    val activeColor = if (isDarkTheme) CoolGray.copy(alpha = 0.9f) else GalacticGray.copy(alpha = 0.9f)
    val animationDuration = 600
    val staggerDelay = 120
    val barWidth = 4.dp
    val barHeight = 18.dp
    val minScaleY = 0.3f
    val maxScaleY = 1.2f

    @Composable
    fun AnimatedBar(animationDelay: Int) {
        val infiniteTransition = rememberInfiniteTransition(label = "WaveBar")
        val animatedScaleY by infiniteTransition.animateFloat(
            initialValue = minScaleY,
            targetValue = maxScaleY,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    delayMillis = animationDelay,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "barScaleY"
        )
        val animatedColor by infiniteTransition.animateColor(
            initialValue = baseColor,
            targetValue = activeColor,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    delayMillis = animationDelay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "barColor"
        )
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight)
                .graphicsLayer {
                    scaleY = animatedScaleY
                }
                .background(animatedColor, shape = RoundedCornerShape(50))
        )
    }

    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 0 until barCount) {
            AnimatedBar(animationDelay = i * staggerDelay)
        }
    }
}

@Composable
internal fun ModelLoadingIndicator(isDarkTheme: Boolean) {
    val bubbleShape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(bubbleShape)
                .background(brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode)
                .border(
                    width = 0.55.dp,
                    brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                    shape = bubbleShape
                )
                .padding(horizontal = PaddingLarge, vertical = PaddingLarge)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypingIndicator(isDarkTheme = isDarkTheme)
                Text(
                    text = "Loading model...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }
    }
}

@Composable
private fun MessageContent(responseText: String, isDarkTheme: Boolean) {
    val parsedContent = remember(responseText, isDarkTheme) {
        parseMarkdownAndCode(responseText, isDarkTheme)
    }

    Column {
        parsedContent.forEachIndexed { index, contentPart ->
            when (contentPart) {
                is ParsedContent.TextContent -> {
                    if (contentPart.text.isNotBlank()) {
                        Text(
                            text = contentPart.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                color = if (isDarkTheme) PureWhite else SlateBlack,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 25.sp
                            )
                        )
                    }
                }
                is ParsedContent.CodeContent -> {
                    if (index > 0) {
                        val prevContent = parsedContent[index - 1]
                        if (prevContent is ParsedContent.TextContent && prevContent.text.isNotBlank()) {
                            Spacer(modifier = Modifier.height(PaddingSmall))
                        }
                    }
                    CodeBlock(
                        language = contentPart.language,
                        code = contentPart.code,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentImages(uris: List<String>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .height(if (uris.size > 2) 160.dp else 80.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PaddingTiny),
        verticalArrangement = Arrangement.spacedBy(PaddingTiny)
    ) {
        items(uris) { uriString ->
            AsyncImage(
                model = uriString,
                contentDescription = "Attached image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun ThinkingBlock(
    thinkingText: String,
    isThinkingVisible: Boolean,
    isDarkTheme: Boolean,
    onToggleVisibility: () -> Unit
) {
    TextButton(
        onClick = onToggleVisibility,
        modifier = Modifier
            .height(48.dp)
            .padding(bottom = PaddingSmall)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Psychology,
                contentDescription = "Thinking Process",
                tint = if (isDarkTheme) CloudWhite else OnyxBlack,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(PaddingExtraSmall))
            Text(
                if (isThinkingVisible) "Hide thought" else "Show thought",
                fontSize = 14.sp,
                color = if (isDarkTheme) CloudWhite else OnyxBlack,
                fontWeight = FontWeight.Medium
            )
        }
    }
    AnimatedVisibility(visible = isThinkingVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = PaddingMedium)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(PaddingMedium)
        ) {
            Text(
                text = thinkingText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = (if (isDarkTheme) WhiteTranslucent else OnyxBlack).copy(alpha = 0.8f),
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun CodeBlock(
    language: String,
    code: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f))
                    .padding(horizontal = PaddingMedium, vertical = PaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = language.ifEmpty { "code" }.lowercase(),
                    color = if (isDarkTheme) CoolGray else GalacticGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        Toast
                            .makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (isDarkTheme) CoolGray else GalacticGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = code,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = if (isDarkTheme) PureWhite.copy(alpha = 0.9f) else SlateBlack.copy(alpha = 0.9f),
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(PaddingMedium)
            )
        }
    }
}

private fun parseThoughtAndResponse(content: String): Pair<String?, String> {
    val thinkingRegex = Regex("""◁think▷(.*?)◁/think▷""", RegexOption.DOT_MATCHES_ALL)
    val match = thinkingRegex.find(content)
    return if (match != null) {
        val thought = match.groupValues[1].trim()
        val response = content.replace(thinkingRegex, "").trim()
        thought to response
    } else {
        null to content
    }
}

private fun parseMarkdownAndCode(text: String, isDarkTheme: Boolean): List<ParsedContent> {
    val contentParts = mutableListOf<ParsedContent>()
    val codeBlockRegex = Regex("```(\\w*)\\n?(.*?)```", setOf(RegexOption.DOT_MATCHES_ALL))

    var lastIndex = 0
    codeBlockRegex.findAll(text).forEach { matchResult ->
        val beforeText = text.substring(lastIndex, matchResult.range.first)
        if (beforeText.trim().isNotEmpty()) {
            contentParts.add(ParsedContent.TextContent(parseMarkdown(beforeText, isDarkTheme)))
        }
        val language = matchResult.groupValues[1].trim()
        val code = matchResult.groupValues[2].trim()
        contentParts.add(ParsedContent.CodeContent(language, code))
        lastIndex = matchResult.range.last + 1
    }

    if (lastIndex < text.length) {
        val remainingText = text.substring(lastIndex)
        if (remainingText.trim().isNotEmpty()) {
            contentParts.add(ParsedContent.TextContent(parseMarkdown(remainingText, isDarkTheme)))
        }
    }

    if (contentParts.isEmpty() && text.isNotEmpty()) {
        contentParts.add(ParsedContent.TextContent(parseMarkdown(text, isDarkTheme)))
    }
    return contentParts
}

private fun parseMarkdown(text: String, isDarkTheme: Boolean): AnnotatedString {
    val builder = buildAnnotatedString {
        val mainTextColor = if (isDarkTheme) PureWhite else SlateBlack

        text.lines().forEach { line ->
            val lineStartIndexInBuilder = length
            val trimmed = line.trim()
            val header1Regex = Regex("^# (.*)")
            val header2Regex = Regex("^## (.*)")
            val blockQuoteRegex = Regex("^> (.*)")
            val bulletListRegex = Regex("^- (.*)")

            val handledAsBlock = when {
                header1Regex.matches(trimmed) -> {
                    val content = header1Regex.find(trimmed)!!.groupValues[1]
                    append(content)
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = mainTextColor), lineStartIndexInBuilder, length)
                    true
                }
                header2Regex.matches(trimmed) -> {
                    val content = header2Regex.find(trimmed)!!.groupValues[1]
                    append(content)
                    addStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = mainTextColor), lineStartIndexInBuilder, length)
                    true
                }
                blockQuoteRegex.matches(trimmed) -> {
                    val content = "❝ " + blockQuoteRegex.find(trimmed)!!.groupValues[1]
                    append(content)
                    addStyle(SpanStyle(fontStyle = FontStyle.Italic, color = mainTextColor.copy(alpha = 0.8f)), lineStartIndexInBuilder, length)
                    true
                }
                bulletListRegex.matches(trimmed) -> {
                    val content = "• " + bulletListRegex.find(trimmed)!!.groupValues[1]
                    append(content)
                    true
                }
                else -> false
            }

            if (!handledAsBlock) {
                val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
                val italicRegex = Regex("\\*(.*?)\\*")
                val codeRegex = Regex("`(.*?)`")
                val allMatches = mutableListOf<Pair<MatchResult, String>>()

                boldRegex.findAll(line).forEach { allMatches.add(it to "bold") }
                italicRegex.findAll(line).forEach { allMatches.add(it to "italic") }
                codeRegex.findAll(line).forEach { allMatches.add(it to "code") }

                allMatches.sortBy { it.first.range.first }

                var lastIndex = 0
                for ((match, type) in allMatches) {
                    if (lastIndex < match.range.first) {
                        append(line.substring(lastIndex, match.range.first))
                    }
                    val matchedText = match.groupValues[1]
                    val startInBuilder = length
                    append(matchedText)
                    val style = when (type) {
                        "code" -> SpanStyle(background = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f), fontFamily = FontFamily.Monospace)
                        "bold" -> SpanStyle(fontWeight = FontWeight.Bold)
                        "italic" -> SpanStyle(fontStyle = FontStyle.Italic)
                        else -> SpanStyle()
                    }
                    addStyle(style, startInBuilder, length)
                    lastIndex = match.range.last + 1
                }

                if (lastIndex < line.length) {
                    append(line.substring(lastIndex))
                }
            }
            if (length > lineStartIndexInBuilder) {
                append("\n")
            }
        }

    }
    return builder
}


internal val PaddingExtraSmall = 6.dp

private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp
private val paddingUltraLarge = 80.dp

@Preview(name = "Chat Message Item - User - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ChatMessageItemUserLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessageItem(
            message = ChatMessage(id = 1, content = "Hello, this is a user message.", isUser = true, sessionId = 1L),
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
            message = ChatMessage(id = 1, content = "Hello, this is a user message in dark mode.", isUser = true, sessionId = 1L),
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
            message = ChatMessage(id = 2, content = "◁think▷The user said hello. I will respond in kind and offer assistance.◁/think▷# Header\nThis is a response.\n- Item 1\n- Item 2", isUser = false, sessionId = 1L),
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
            message = ChatMessage(id = 2, content = "◁think▷This is my thought process for the response in dark mode.◁/think▷Hi there! I'm the model in dark mode.", isUser = false, sessionId = 1L),
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
            onPauseGeneration = {}
        )
    }
}

@Preview(name = "Model Response Item - Streaming - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ModelResponseItemStreamingLightPreview() {
    BotChatTheme(darkTheme = false) {
        ModelResponseItem(
            content = "◁think▷The user is asking a question during streaming. I will formulate a response.◁/think▷This is a streamed response...",
            isThinking = false,
            isDarkTheme = false,
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
            isModelLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false
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
            isModelLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false
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
            isModelLoading = false,
            theme = "gradient",
            streamingMessage = "",
            isDarkTheme = true
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
            isModelLoading = false,
            theme = "default",
            streamingMessage = "◁think▷The user is asking about Kotlin.◁/think▷Kotlin is a programming language...",
            isDarkTheme = true
        )
    }
}

@Preview(name = "Markdown Parsing - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MarkdownParsingPreview() {
    BotChatTheme(darkTheme = false) {
        val markdownText = "# Header\nThis is **bold** and *italic*.\n> This is a quote.\n- List item 1\n- List item 2"
        Box(
            Modifier
                .padding(16.dp)
                .background(Color.White)
        ) {
            Text(parseMarkdown(markdownText, isDarkTheme = false))
        }
    }
}

