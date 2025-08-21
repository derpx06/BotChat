package com.example.ChatBlaze.ui.components.chat

import BotChatTheme
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.data.database.ChatMessage
import com.example.ChatBlaze.ui.theme.AccentIndigo
import com.example.ChatBlaze.ui.theme.AstralBlue
import com.example.ChatBlaze.ui.theme.BackgroundGradientDark
import com.example.ChatBlaze.ui.theme.BackgroundGradientVibrantBlue
import com.example.ChatBlaze.ui.theme.Black
import com.example.ChatBlaze.ui.theme.BottomFadeGradientDark
import com.example.ChatBlaze.ui.theme.BottomFadeGradientLight
import com.example.ChatBlaze.ui.theme.ChatBubbleGradientDark
import com.example.ChatBlaze.ui.theme.ChatBubbleGradientLight
import com.example.ChatBlaze.ui.theme.ChatInterfaceGradientDark
import com.example.ChatBlaze.ui.theme.ChatInterfaceGradientLight
import com.example.ChatBlaze.ui.theme.CloudWhite
import com.example.ChatBlaze.ui.theme.CoolGray
import com.example.ChatBlaze.ui.theme.ElectricCyan
import com.example.ChatBlaze.ui.theme.GalacticGray
import com.example.ChatBlaze.ui.theme.MidnightBlack
import com.example.ChatBlaze.ui.theme.OnyxBlack
import com.example.ChatBlaze.ui.theme.PureWhite
import com.example.ChatBlaze.ui.theme.ResponseGradientDarkMode
import com.example.ChatBlaze.ui.theme.ResponseGradientLightMode
import com.example.ChatBlaze.ui.theme.SapphireBlue
import com.example.ChatBlaze.ui.theme.SlateBlack
import com.example.ChatBlaze.ui.theme.TopBarUnderlineDark
import com.example.ChatBlaze.ui.theme.TopBarUnderlineLight
import com.example.ChatBlaze.ui.theme.WhiteTranslucent
import kotlinx.coroutines.launch


private sealed class ParsedContent {
    data class TextContent(val text: AnnotatedString) : ParsedContent()
    data class CodeContent(val language: String, val code: String) : ParsedContent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    theme: String,
    streamingMessage: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onPauseGeneration: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isLoading, streamingMessage.isNotEmpty()) {
        val totalItems = listState.layoutInfo.totalItemsCount
        if (totalItems > 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(totalItems - 1)
            }
        }
    }

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
            .padding(PaddingTiny)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when {
            messages.isEmpty() && !isLoading && streamingMessage.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PaddingExtraLarge),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hey there! Let's get started. 😊\nWhat's on your mind?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDarkTheme) GalacticGray else OnyxBlack,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PaddingMedium),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(PaddingLarge),
                contentPadding = PaddingValues(bottom = paddingUltraLarge, top = PaddingMedium)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        modifier = Modifier
                            .animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                            .padding(vertical = 4.dp)
                    )
                }
                if ((isLoading || streamingMessage.isNotEmpty())) {
                    item(key = "model_response_item") {
                        ModelResponseItem(
                            content = streamingMessage,
                            isThinking = isLoading && streamingMessage.isEmpty(),
                            isDarkTheme = isDarkTheme,
                            onPauseGeneration = onPauseGeneration,
                            modifier = Modifier
                                .animateItemPlacement(
                                    animationSpec = tween(durationMillis = 300)
                                )
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isUser

    if (isUserMessage) {
        val bubbleShape =
            RoundedCornerShape(topStart = 24.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(bubbleShape)
                    .then(
                        if (theme == "gradient") Modifier.background(
                            brush = if (isDarkTheme) ChatBubbleGradientDark else ChatBubbleGradientLight,
                            alpha = 0.9f
                        )
                        else Modifier.background(
                            color = if (isDarkTheme) AstralBlue.copy(alpha = 0.9f) else AccentIndigo.copy(
                                alpha = 0.9f
                            )
                        )
                    )
                    .border(
                        width = 0.75.dp,
                        brush = if (isDarkTheme) BottomFadeGradientDark else BottomFadeGradientLight,
                        shape = bubbleShape
                    )
                    .padding(horizontal = PaddingLarge, vertical = PaddingLarge)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp,
                        letterSpacing = 0.4.sp
                    )
                )
            }
        }
    } else {
        var isThinkingVisible by remember { mutableStateOf(false) }
        val (thinkingText, responseText) = remember(message.content) {
            parseThoughtAndResponse(message.content)
        }
        val bubbleShape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 24.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(bubbleShape)
                    .background(
                        brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                        alpha = 0.95f
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

                    val parsedContent = remember(responseText, isDarkTheme) {
                        parseMarkdownAndCode(responseText, isDarkTheme)
                    }

                    if (parsedContent.isNotEmpty()) {
                        parsedContent.forEach { contentPart ->
                            when (contentPart) {
                                is ParsedContent.TextContent -> {
                                    if (contentPart.text.isNotEmpty()) {
                                        Text(
                                            text = contentPart.text,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 16.sp,
                                                color = if (isDarkTheme) PureWhite else SlateBlack,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 25.sp,
                                                letterSpacing = 0.4.sp
                                            )
                                        )
                                    }
                                }

                                is ParsedContent.CodeContent -> {
                                    CodeBlock(
                                        language = contentPart.language,
                                        code = contentPart.code,
                                        isDarkTheme = isDarkTheme,
                                        modifier = Modifier.padding(top = PaddingSmall)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ModelResponseItem(
    content: String,
    isThinking: Boolean,
    isDarkTheme: Boolean,
    onPauseGeneration: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isThinkingVisible by remember { mutableStateOf(false) }
    val (thinkingText, responseText) = remember(content) {
        parseThoughtAndResponse(content)
    }
    val bubbleShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 24.dp,
        bottomStart = 24.dp,
        bottomEnd = 24.dp
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(bubbleShape)
                .background(
                    brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                    alpha = 0.95f
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
                    val parsedContent = remember(responseText, isDarkTheme) {
                        parseMarkdownAndCode(responseText, isDarkTheme)
                    }
                    if (parsedContent.isNotEmpty()) {
                        parsedContent.forEach { contentPart ->
                            when (contentPart) {
                                is ParsedContent.TextContent -> {
                                    if (contentPart.text.isNotEmpty()) {
                                        Text(
                                            text = contentPart.text,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 16.sp,
                                                color = if (isDarkTheme) PureWhite else SlateBlack,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 25.sp,
                                                letterSpacing = 0.4.sp
                                            )
                                        )
                                    }
                                }

                                is ParsedContent.CodeContent -> {
                                    CodeBlock(
                                        language = contentPart.language,
                                        code = contentPart.code,
                                        isDarkTheme = isDarkTheme,
                                        modifier = Modifier.padding(top = PaddingSmall)
                                    )
                                }
                            }
                        }
                    }
                }
            }
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
                .background(
                    if (isDarkTheme) Color.White.copy(alpha = 0.05f)
                    else Color.Black.copy(alpha = 0.05f)
                )
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.1f)
                    else Color.Black.copy(alpha = 0.1f),
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
            .background(
                if (isDarkTheme) Color.Black.copy(alpha = 0.4f)
                else Color.Black.copy(alpha = 0.05f)
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.1f)
                else Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDarkTheme) Color.White.copy(alpha = 0.1f)
                        else Color.Black.copy(alpha = 0.08f)
                    )
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
                    color = if (isDarkTheme) PureWhite.copy(alpha = 0.9f) else SlateBlack.copy(
                        alpha = 0.9f
                    ),
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(PaddingMedium)
            )
        }
    }
}


@Composable
fun TypingIndicator(
    isDarkTheme: Boolean,
    barCount: Int = 5
) {
    val baseColor = if (isDarkTheme) CoolGray.copy(alpha = 0.5f) else GalacticGray.copy(alpha = 0.5f)
    val activeColor = if (isDarkTheme) CoolGray.copy(alpha = 0.9f) else GalacticGray.copy(alpha = 0.9f)
    val animationDuration = 500
    val staggerDelay = 100
    val barWidth = 4.dp
    val barHeight = 16.dp
    val minScaleY = 0.3f
    val maxScaleY = 1.0f

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
    val builder = AnnotatedString.Builder()
    var currentIndex = 0
    val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
    val italicRegex = Regex("\\*(.*?)\\*")
    val codeRegex = Regex("`(.*?)`")
    val header1Regex = Regex("^# (.*)")
    val header2Regex = Regex("^## (.*)")
    val blockQuoteRegex = Regex("^> (.*)")
    val bulletListRegex = Regex("^- (.*)")

    val mainTextColor = if (isDarkTheme) PureWhite else SlateBlack

    text.split("\n").forEach { line ->
        val trimmed = line.trim()
        when {
            header1Regex.matches(trimmed) -> {
                val headerText = header1Regex.find(trimmed)!!.groupValues[1] + "\n"
                builder.append(headerText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = mainTextColor
                    ),
                    currentIndex,
                    currentIndex + headerText.length
                )
                currentIndex += headerText.length
            }

            header2Regex.matches(trimmed) -> {
                val subheaderText = header2Regex.find(trimmed)!!.groupValues[1] + "\n"
                builder.append(subheaderText)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = mainTextColor
                    ),
                    currentIndex,
                    currentIndex + subheaderText.length
                )
                currentIndex += subheaderText.length
            }

            blockQuoteRegex.matches(trimmed) -> {
                val quoteText = "❝ " + blockQuoteRegex.find(trimmed)!!.groupValues[1] + "\n"
                builder.append(quoteText)
                builder.addStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = mainTextColor.copy(alpha = 0.8f)
                    ),
                    currentIndex,
                    currentIndex + quoteText.length
                )
                currentIndex += quoteText.length
            }

            bulletListRegex.matches(trimmed) -> {
                val itemText = "• " + bulletListRegex.find(trimmed)!!.groupValues[1] + "\n"
                builder.append(itemText)
                currentIndex += itemText.length
            }

            else -> {
                var remainingLine = line
                while (remainingLine.isNotEmpty()) {
                    val codeMatch = codeRegex.find(remainingLine)
                    val boldMatch = boldRegex.find(remainingLine)
                    val italicMatch = italicRegex.find(remainingLine)

                    val earliest = listOfNotNull(
                        codeMatch?.let { it to "code" },
                        boldMatch?.let { it to "bold" },
                        italicMatch?.let { it to "italic" }
                    ).minByOrNull { it.first.range.first }

                    if (earliest == null) {
                        builder.append(remainingLine + "\n")
                        currentIndex += remainingLine.length + 1
                        break
                    }

                    val match = earliest.first
                    val type = earliest.second
                    val beforeText = remainingLine.substring(0, match.range.first)
                    if (beforeText.isNotEmpty()) {
                        builder.append(beforeText)
                        currentIndex += beforeText.length
                    }

                    val matchedText = match.groupValues[1]
                    builder.append(matchedText)
                    val style = when (type) {
                        "code" -> SpanStyle(
                            background = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(
                                alpha = 0.08f
                            ),
                            fontFamily = FontFamily.Monospace
                        )

                        "bold" -> SpanStyle(fontWeight = FontWeight.Bold)
                        "italic" -> SpanStyle(fontStyle = FontStyle.Italic)
                        else -> SpanStyle()
                    }
                    builder.addStyle(style, currentIndex, currentIndex + matchedText.length)
                    currentIndex += matchedText.length
                    remainingLine = remainingLine.substring(match.range.last + 1)
                }
            }
        }
    }
    return builder.toAnnotatedString()
}


private val PaddingTiny = 4.dp
internal val PaddingExtraSmall = 6.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 24.dp
private val paddingUltraLarge = 80.dp

@Preview(name = "Chat Message Item - User - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ChatMessageItemUserLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessageItem(
            message = ChatMessage(
                id = 1,
                content = "Hello, this is a user message.",
                isUser = true,
                timestamp = System.currentTimeMillis(),
                sessionId = 1L
            ),
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
            message = ChatMessage(
                id = 1,
                content = "Hello, this is a user message in dark mode.",
                isUser = true,
                timestamp = System.currentTimeMillis(),
                sessionId = 1L
            ),
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
            message = ChatMessage(
                id = 2,
                content = "◁think▷The user said hello. I will respond in kind and offer assistance.◁/think▷# Header\nThis is a response.\n- Item 1\n- Item 2",
                isUser = false,
                timestamp = System.currentTimeMillis(),
                sessionId = 1L
            ),
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
            message = ChatMessage(
                id = 2,
                content = "◁think▷This is my thought process for the response in dark mode.◁/think▷Hi there! I'm the model in dark mode.",
                isUser = false,
                timestamp = System.currentTimeMillis(),
                sessionId = 1L
            ),
            isDarkTheme = true,
            theme = "default"
        )
    }
}

@Preview(
    name = "Model Response Item - Thinking - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
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

@Preview(
    name = "Model Response Item - Streaming - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
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

@Preview(
    name = "Chat Messages - Empty - Light",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ChatMessagesEmptyLightPreview() {
    BotChatTheme(darkTheme = false) {
        ChatMessages(
            messages = emptyList(),
            isLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false
        )
    }
}

@Preview(
    name = "Chat Messages - With Content - Light",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ChatMessagesWithContentLightPreview() {
    BotChatTheme(darkTheme = false) {
        val sampleMessages = listOf(
            ChatMessage(
                id = 1,
                content = "Hello!",
                isUser = true,
                timestamp = System.currentTimeMillis() - 2000,
                sessionId = 1L
            ),
            ChatMessage(
                id = 2,
                content = "Hi there!",
                isUser = false,
                timestamp = System.currentTimeMillis() - 1000,
                sessionId = 1L
            )
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = false,
            theme = "default",
            streamingMessage = "",
            isDarkTheme = false
        )
    }
}

@Preview(
    name = "Chat Messages - Loading - Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ChatMessagesLoadingDarkPreview() {
    BotChatTheme(darkTheme = true) {
        val sampleMessages = listOf(
            ChatMessage(
                id = 1,
                content = "Question?",
                isUser = true,
                timestamp = System.currentTimeMillis() - 1000,
                sessionId = 1L
            )
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = true,
            theme = "gradient",
            streamingMessage = "",
            isDarkTheme = true
        )
    }
}

@Preview(
    name = "Chat Messages - Streaming - Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ChatMessagesStreamingDarkPreview() {
    BotChatTheme(darkTheme = true) {
        val sampleMessages = listOf(
            ChatMessage(
                id = 1,
                content = "What is Kotlin?",
                isUser = true,
                timestamp = System.currentTimeMillis() - 1000,
                sessionId = 1L
            )
        )
        ChatMessages(
            messages = sampleMessages,
            isLoading = false,
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
