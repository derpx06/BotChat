package com.example.chatapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.Data.ChatMessage
import com.example.botchat.R
import com.example.chatapp.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0A1128), Color(0xFF1A2A44))
)
private val ChatInterfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF101827), Color(0xFF1F2A44)) // Softer, calming deep blue to gray
)
private val TopBarGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6D28D9), Color(0xFFD946EF)) // Vibrant purple to magenta
)
private val InputFieldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF1E2A47).copy(alpha = 0.85f), Color(0xFF2A3B61).copy(alpha = 0.85f))
)
private val MessageGlow = Color(0xFF6D28D9).copy(alpha = 0.15f)
private val InputBorderGlow = Brush.linearGradient(
    colors = listOf(Color(0xFF6D28D9).copy(alpha = 0.35f), Color(0xFFD946EF).copy(alpha = 0.35f))
)

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val uiState by viewModel.uiState
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        // Chat Interface (Messages)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 68.dp) // Adjusted for top and bottom bar overlap
                .clip(RoundedCornerShape(12.dp))
                .background(ChatInterfaceGradient)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatMessageItem(message = message)
                }
                item {
                    AnimatedVisibility(
                        visible = uiState.isLoading,
                        enter = fadeIn(animationSpec = tween(400)) + expandVertically(animationSpec = spring(dampingRatio = 0.8f)),
                        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                    ) {
                        ThinkingIndicator(modifier = Modifier.padding(horizontal = 6.dp))
                    }
                }
            }
        }

        // Top Bar (Header)
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { -it } + fadeIn() + scaleIn(initialScale = 0.95f, animationSpec = tween(600, easing = FastOutSlowInEasing)),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .height(48.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            color = Color.White.copy(alpha = 0.1f), // Simulated blur effect
                            style = androidx.compose.ui.graphics.drawscope.Fill
                        )
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(TopBarGradient)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { /* Menu logic */ }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_more),
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Bar (Input Section)
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it } + fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .navigationBarsPadding()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = InputBorderGlow,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f),
                            alpha = 0.7f
                        )
                    },
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(InputFieldGradient)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.inputText,
                            onValueChange = viewModel::updateInputText,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        color = MessageGlow,
                                        style = androidx.compose.ui.graphics.drawscope.Fill,
                                        alpha = if (uiState.inputText.isNotEmpty()) 0.35f else 0.15f
                                    )
                                },
                            placeholder = {
                                Text(
                                    "Message AI Assistant...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 13.sp
                                    )
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = { viewModel.sendMessage() }
                            ),
                            shape = RoundedCornerShape(18.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.White
                            ),
                            maxLines = 2
                        )
                        AnimatedSendButton(
                            onClick = viewModel::sendMessage,
                            enabled = uiState.inputText.isNotBlank() && !uiState.isLoading,
                            modifier = Modifier.size(40.dp),
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }
        }

        // Error Dialog
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn() + scaleIn(initialScale = 0.95f, animationSpec = tween(300, easing = FastOutSlowInEasing)),
            exit = fadeOut() + scaleOut(targetScale = 0.95f, animationSpec = tween(300))
        ) {
            if (uiState.errorMessage != null) {
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    title = {
                        Text(
                            "Error",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    },
                    text = {
                        Text(
                            uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = viewModel::clearError) {
                            Text(
                                "OK",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFD946EF)
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = Color(0xFF1E2A47),
                    modifier = Modifier.shadow(6.dp, RoundedCornerShape(14.dp))
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleGradient = if (message.isUser)
        Brush.linearGradient(colors = listOf(Color(0xFF6D28D9), Color(0xFFD946EF)))
    else
        InputFieldGradient

    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(
            initialOffsetX = { if (message.isUser) it else -it },
            animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f)
        ) + fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) + scaleIn(
            initialScale = 0.98f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(300)) + shrinkOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .wrapContentWidth(alignment)
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 250.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            color = MessageGlow,
                            style = androidx.compose.ui.graphics.drawscope.Fill,
                            alpha = 0.2f
                        )
                    },
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(bubbleGradient)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        )
                        Text(
                            text = SimpleDateFormat("HH:mm").format(message.timestamp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSendButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.9f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing)
        ),
        label = "rotation"
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .graphicsLayer(rotationZ = rotation)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors = listOf(Color(0xFF6D28D9), Color(0xFFD946EF))))
            .drawWithContent {
                drawContent()
                drawRect(
                    color = MessageGlow,
                    style = androidx.compose.ui.graphics.drawscope.Fill,
                    alpha = 0.3f
                )
            }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 1.5.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ThinkingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = MessageGlow,
                        style = androidx.compose.ui.graphics.drawscope.Fill,
                        alpha = 0.2f
                    )
                },
            shape = RoundedCornerShape(14.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(InputFieldGradient)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(14.dp)
                            .alpha(alpha),
                        color = Color.White,
                        strokeWidth = 1.5.dp
                    )
                    Text(
                        text = "AI is thinking...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = alpha),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}