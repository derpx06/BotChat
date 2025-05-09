package com.example.botchat.ui.components.guides

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenRouterApiGuideScreen(
    isDarkTheme: Boolean = true,
    onBackClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        Text(
                            text = "OpenRouter API Key Setup",
                            color = if (isDarkTheme) PureWhite else SlateBlack,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                        // Shimmer effect for header
                        val infiniteTransition = rememberInfiniteTransition(label = "Shimmer")
                        val shimmerOffset by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "ShimmerOffset"
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .drawWithContent {
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                PureWhite.copy(alpha = 0.1f),
                                                Transparent,
                                                PureWhite.copy(alpha = 0.1f)
                                            ),
                                            start = Offset(shimmerOffset * size.width, 0f),
                                            end = Offset((shimmerOffset + 1f) * size.width, 0f)
                                        ),
                                        alpha = 0.3f
                                    )
                                }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) ElectricCyan else Purple40
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) StarlitPurple else MistGray
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = if (isDarkTheme) MidnightBlack else CloudWhite
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Get Your OpenRouter API Key",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) ElectricCyan else Purple40,
                        fontSize = 26.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Follow these steps to sign up for OpenRouter and generate your API key for use in your app.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isDarkTheme) PureWhite.copy(alpha = 0.8f) else SlateBlack.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                )
            }

            // Step 1: Create an OpenRouter Account
            item {
                GuideStep(
                    stepNumber = 1,
                    title = "Create an OpenRouter Account",
                    content = {
                        val annotatedText = buildAnnotatedString {
                            append("1. Visit the OpenRouter website at ")
                            val startIndex = length
                            append("openrouter.ai")
                            val endIndex = length
                            addStyle(
                                style = SpanStyle(
                                    color = if (isDarkTheme) ElectricCyan else Purple40,
                                    textDecoration = TextDecoration.Underline
                                ),
                                start = startIndex,
                                end = endIndex
                            )
                            addStringAnnotation(
                                tag = "URL",
                                annotation = "https://openrouter.ai",
                                start = startIndex,
                                end = endIndex
                            )
                            append(".\n")
                            append("2. Click 'Sign Up' and fill in your details (email, password).\n")
                            append("3. Verify your email by checking your inbox (and spam folder) for a confirmation link.\n")
                            append("4. Log in to your new account.\n")
                            append("5. Review the terms of service at openrouter.ai/terms for details.")
                        }
                        val uriHandler = LocalUriHandler.current
                        ClickableText(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isDarkTheme) PureWhite else SlateBlack
                            ),
                            onClick = { offset ->
                                annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        try {
                                            uriHandler.openUri(annotation.item)
                                        } catch (e: Exception) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Failed to open URL: ${e.message}")
                                            }
                                        }
                                    }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                try {
                                    uriHandler.openUri("https://openrouter.ai")
                                } catch (e: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to open URL: ${e.message}")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                                contentColor = if (isDarkTheme) MidnightBlack else PureWhite
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .shadow(2.dp, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Open URL",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open OpenRouter Website",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    isDarkTheme = isDarkTheme
                )
            }

            // Step 2: Generate an API Key
            item {
                GuideStep(
                    stepNumber = 2,
                    title = "Generate and Copy Your API Key",
                    content = {
                        val annotatedText = buildAnnotatedString {
                            append("1. Log in to your OpenRouter account at openrouter.ai.\n")
                            append("2. Navigate to the 'Keys' section in your dashboard (usually under account settings).\n")
                            append("3. Click 'Create Key' or 'Generate New Key'.\n")
                            append("4. Name the key (e.g., 'BotChatApp') for reference.\n")
                            append("5. Copy the generated API key to a secure location.\n")
                            append("For more help, see the documentation at ")
                            val startIndex = length
                            append("openrouter.ai/docs")
                            val endIndex = length
                            addStyle(
                                style = SpanStyle(
                                    color = if (isDarkTheme) ElectricCyan else Purple40,
                                    textDecoration = TextDecoration.Underline
                                ),
                                start = startIndex,
                                end = endIndex
                            )
                            addStringAnnotation(
                                tag = "URL",
                                annotation = "https://openrouter.ai/docs",
                                start = startIndex,
                                end = endIndex
                            )
                            append(".")
                        }
                        val uriHandler = LocalUriHandler.current
                        ClickableText(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isDarkTheme) PureWhite else SlateBlack
                            ),
                            onClick = { offset ->
                                annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        try {
                                            uriHandler.openUri(annotation.item)
                                        } catch (e: Exception) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Failed to open URL: ${e.message}")
                                            }
                                        }
                                    }
                            }
                        )
                    },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun GuideStep(
    stepNumber: Int,
    title: String,
    content: @Composable () -> Unit,
    isDarkTheme: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 100),
        label = "CardScale"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isDarkTheme)
                        listOf(StarlitPurple, MidnightBlack.copy(alpha = 0.8f))
                    else
                        listOf(MistGray, CloudWhite.copy(alpha = 0.8f))
                )
            )
            .scale(scaleAnimation)
            .clickable(
                onClick = {
                    expanded = !expanded
                    scale = if (expanded) 1.02f else 1f
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $stepNumber: $title",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) ElectricCyan else Purple40,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = if (isDarkTheme) ElectricCyan else Purple40,
                    modifier = Modifier.size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Theme")
@Preview(showBackground = true, widthDp = 600, heightDp = 800, name = "Tablet")
@Preview(showBackground = true, locale = "en-rUS", name = "English Locale")
@Composable
fun OpenRouterApiGuideScreenPreview(
    @PreviewParameter(ThemePreviewProvider::class) isDarkTheme: Boolean
) {
    BotChatTheme {
        OpenRouterApiGuideScreen(
            isDarkTheme = isDarkTheme,
            onBackClick = {}
        )
    }
}

class ThemePreviewProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}
