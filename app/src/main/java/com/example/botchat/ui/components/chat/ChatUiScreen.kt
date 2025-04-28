//package com.example.botchat.ui.components.chat
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.RoundRect
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.graphics.drawscope.Fill
//import androidx.compose.ui.hapticfeedback.HapticFeedbackType
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.platform.LocalHapticFeedback
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Density
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.zIndex
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.botchat.Data.ChatMessage
//import com.example.botchat.R
//import com.example.botchat.viewmodel.SettingViewModel
//import com.example.chatapp.viewmodel.ChatViewModel
//import kotlin.random.Random
//import kotlinx.coroutines.launch
//
//private val BackgroundGradient = Brush.verticalGradient(
//    colors = listOf(Color(0xFF0A0E14), Color(0xFF1A2333), Color(0xFF2D3748))
//)
//val ChatInterfaceGradient = Brush.verticalGradient(
//    colors = listOf(
//        Color(0xFF1E1B4B), // Deep indigo
//        Color(0xFF3B0764), // Soft purple
//        Color(0xFF0F766E)  // Muted teal
//    )
//)
//private val InputFieldGradient = Brush.horizontalGradient(
//    colors = listOf(Color(0xFF2D3748).copy(alpha = 0.5f), Color(0xFF4B5563).copy(alpha = 0.5f))
//)
//val BottomFadeGradient = Brush.verticalGradient(
//    colors = listOf(Color(0xFF0F766E), Color.Transparent)
//)
//private val MessageGlow = Color(0xFF6D28D9).copy(alpha = 0.15f)
//private val TopBarUnderline = Brush.linearGradient(
//    colors = listOf(Color(0xFF6D28D9).copy(alpha = 0.3f), Color(0xFFA855F7).copy(alpha = 0.3f))
//)
//private val InputUnderline = Brush.linearGradient(
//    colors = listOf(Color(0xFF6D28D9), Color(0xFFC026D3))
//)
//private val SendButtonGradient = Brush.linearGradient(
//    colors = listOf(Color(0xFF6D28D9), Color(0xFFA855F7))
//)
//private val InactiveSendButtonGradient = Brush.linearGradient(
//    colors = listOf(Color(0xFF4B5563), Color(0xFF4B5563))
//)
//private val StopButtonGradient = Brush.linearGradient(
//    colors = listOf(Color(0xFFEF4444), Color(0xFFF87171))
//)
//
//@Preview
//@Composable
//fun ChatScreen(viewModel: ChatViewModel = viewModel(),settingViewModel: SettingViewModel=viewModel()) {
//    val uiState by viewModel.uiState
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val density = LocalDensity.current
//    val imeInsets = WindowInsets.ime
//    val imeHeight by remember { derivedStateOf { imeInsets.getBottom(density) } }
//
//    LaunchedEffect(uiState.messages.size) {
//        if (uiState.messages.isNotEmpty()) {
//            listState.animateScrollToItem(uiState.messages.size - 1)
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(BackgroundGradient)
//            .imePadding()
//            .safeDrawingPadding()
//            .statusBarsPadding()
//            .drawWithContent {
//                drawContent()
//                // Subtle background pattern (diagonal lines)
//                drawLine(
//                    color = Color.White.copy(alpha = 0.05f),
//                    start = Offset(0f, 0f),
//                    end = Offset(size.width, size.height),
//                    strokeWidth = 1f
//                )
//                drawLine(
//                    color = Color.White.copy(alpha = 0.05f),
//                    start = Offset(0f, size.height),
//                    end = Offset(size.width, 0f),
//                    strokeWidth = 1f
//                )
//                // Particle background
//                repeat(50) {
//                    val x = Random.nextFloat() * size.width
//                    val y = Random.nextFloat() * size.height
//                    drawCircle(
//                        color = Color.White.copy(alpha = 0.05f),
//                        radius = 2f,
//                        center = Offset(x, y)
//                    )
//                }
//            }
//    ) {
//        // Chat Interface (Messages)
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 56.dp)
//                .clip(RoundedCornerShape(12.dp))
//                .background(ChatInterfaceGradient)
//                .zIndex(0f)
//                .drawWithContent {
//                    drawContent()
//                    // Bottom fade gradient
//                    val fadeHeight = 48.dp.toPx()
//                    drawRect(
//                        brush = BottomFadeGradient,
//                        topLeft = Offset(0f, size.height - fadeHeight),
//                        size = androidx.compose.ui.geometry.Size(size.width, fadeHeight)
//                    )
//                }
//        ) {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(bottom = 48.dp), // Space for bottom fade gradient
//                state = listState,
//                contentPadding = PaddingValues(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(uiState.messages) { message ->
//                    ChatMessageItem(message = message)
//                }
//                item {
//                    AnimatedVisibility(
//                        visible = uiState.isLoading,
//                        enter = fadeIn(animationSpec = tween(400)) + expandVertically(animationSpec = spring(dampingRatio = 0.8f)),
//                        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
//                    ) {
//                        ThinkingIndicator(
//                            modifier = Modifier.padding(horizontal = 8.dp)
//                        )
//                    }
//                }
//            }
//        }
//
//        // Top Bar (Header)
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 0.dp, vertical = 0.dp)
//                .height(56.dp)
//                .shadow(6.dp, shape = ReverseRoundedShape())
//                .drawWithContent {
//                    drawContent()
//                    // Simulated blur effect
//                    drawRect(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
//                        ),
//                        alpha = 0.5f
//                    )
//                    drawRect(
//                        brush = TopBarUnderline,
//                        topLeft = Offset(0f, size.height - 2f),
//                        size = androidx.compose.ui.geometry.Size(size.width, 2f)
//                    )
//                },
//            shape = ReverseRoundedShape(),
//            color = Color(0xFF0F172A).copy(alpha = 0.8f)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(ReverseRoundedShape())
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "AI Assistant",
//                        style = MaterialTheme.typography.titleMedium.copy(
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 16.sp
//                        )
//                    )
//                    Spacer(modifier = Modifier.weight(1f))
//                    val infiniteTransition = rememberInfiniteTransition()
//                    val angle by infiniteTransition.animateFloat(
//                        initialValue = 0f,
//                        targetValue = 360f,
//                        animationSpec = infiniteRepeatable(
//                            animation = tween(8000, easing = LinearEasing)
//                        )
//                    )
//                    IconButton(onClick = {settingViewModel.toggleSettings()}) { }
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_star),
//                        contentDescription = "Logo",
//                        tint = Color.White,
//                        modifier = Modifier
//                            .size(24.dp)
//                            .graphicsLayer { rotationZ = angle }
//                    )
//                }
//            }
//        }
//
//        // Bottom Bar (Input Section)
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 12.dp, vertical = 4.dp)
//                .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
//                .align(Alignment.BottomCenter)
//                .navigationBarsPadding()
//                .zIndex(1f)
//                .drawWithContent {
//                    drawContent()
//                    // Enhanced blur effect with noise texture
//                    drawRect(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.15f))
//                        ),
//                        alpha = 0.85f
//                    )
//                    // Noise texture for frosted glass effect
//                    repeat(150) {
//                        val x = Random.nextFloat() * size.width
//                        val y = Random.nextFloat() * size.height
//                        drawCircle(
//                            color = Color.White.copy(alpha = 0.05f),
//                            radius = 1.5f,
//                            center = Offset(x, y)
//                        )
//                    }
//                },
//            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
//            color = Color.Transparent
//        ) {
//            Box(
//                modifier = Modifier
//                    .background(InputFieldGradient)
//                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(6.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    OutlinedTextField(
//                        value = uiState.inputText,
//                        onValueChange = viewModel::updateInputText,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 6.dp)
//                            .drawWithContent {
//                                drawContent()
//                                val underlineWidth = size.width * (uiState.inputText.length.coerceAtMost(50) / 50f)
//                                drawRect(
//                                    brush = InputUnderline,
//                                    topLeft = Offset(0f, size.height - 2f),
//                                    size = androidx.compose.ui.geometry.Size(underlineWidth, 2f)
//                                )
//                            },
//                        label = {
//                            Text(
//                                "Message AI Assistant...",
//                                style = MaterialTheme.typography.bodySmall.copy(
//                                    color = Color.White.copy(alpha = 0.5f),
//                                    fontSize = 12.sp
//                                )
//                            )
//                        },
//                        textStyle = MaterialTheme.typography.bodyMedium.copy(
//                            color = Color.White,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Medium
//                        ),
//                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
//                        keyboardActions = KeyboardActions(
//                            onSend = {
//                                viewModel.sendMessage()
//                                keyboardController?.hide()
//                            }
//                        ),
//                        shape = RoundedCornerShape(18.dp),
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            cursorColor = Color.White
//                        ),
//                        maxLines = 4
//                    )
//                    AnimatedSendButton(
//                        onSendClick = {
//                            viewModel.sendMessage()
//                            keyboardController?.hide()
//                        },
//                        onStopClick = { viewModel.cancelProcessing() },
//                        enabled = uiState.inputText.isNotBlank() && !uiState.isLoading,
//                        modifier = Modifier.size(40.dp),
//                        isLoading = uiState.isLoading,
//                        isInputEmpty = uiState.inputText.isBlank()
//                    )
//                }
//            }
//        }
//
//        // Error Dialog (Full-screen from bottom)
//        if (uiState.errorMessage != null) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .zIndex(3f)
//            ) {
//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight(0.9f)
//                        .align(Alignment.BottomCenter)
//                        .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//                        .background(
//                            Brush.verticalGradient(
//                                colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
//                            ),
//                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
//                        )
//                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
//                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
//                    color = Color(0xFF2D3748).copy(alpha = 0.9f)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Column {
//                            Text(
//                                text = "Error",
//                                style = MaterialTheme.typography.titleMedium.copy(
//                                    fontWeight = FontWeight.Bold,
//                                    fontSize = 18.sp,
//                                    color = Color.White
//                                )
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = uiState.errorMessage!!,
//                                style = MaterialTheme.typography.bodyMedium.copy(
//                                    fontSize = 14.sp,
//                                    color = Color.White.copy(alpha = 0.8f)
//                                )
//                            )
//                        }
//                        TextButton(
//                            onClick = viewModel::clearError,
//                            modifier = Modifier.align(Alignment.End)
//                        ) {
//                            Text(
//                                text = "OK",
//                                style = MaterialTheme.typography.labelLarge.copy(
//                                    fontWeight = FontWeight.SemiBold,
//                                    fontSize = 14.sp,
//                                    color = Color(0xFFC026D3)
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatMessageItem(message: ChatMessage) {
//    val alignment = if (message.isUser) Alignment.End else Alignment.Start
//    val bubbleGradient = if (message.isUser)
//        Brush.linearGradient(colors = listOf(Color(0xFF6D28D9), Color(0xFFA855F7)))
//    else
//        InputFieldGradient
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(
//                start = if (message.isUser) 64.dp else 16.dp,
//                end = if (message.isUser) 16.dp else 16.dp
//            )
//            .wrapContentWidth(alignment)
//    ) {
//        Surface(
//            modifier = Modifier
//                .widthIn(max = if (message.isUser) 250.dp else Dp.Unspecified)
//                .shadow(4.dp, RoundedCornerShape(14.dp))
//                .drawWithContent {
//                    drawContent()
//                    drawRect(
//                        color = MessageGlow,
//                        style = Fill,
//                        alpha = 0.2f
//                    )
//                },
//            shape = RoundedCornerShape(14.dp),
//            color = Color.Transparent
//        ) {
//            Box(
//                modifier = Modifier
//                    .background(bubbleGradient)
//                    .padding(if (message.isUser) 8.dp else 10.dp)
//            ) {
//                Text(
//                    text = message.content,
//                    style = MaterialTheme.typography.bodyMedium.copy(
//                        fontSize = when (message.content.length) {
//                            in 0..50 -> 14.sp
//                            in 51..100 -> 13.sp
//                            else -> 12.sp
//                        },
//                        fontWeight = FontWeight.Medium,
//                        color = Color.White,
//                        lineHeight = 16.sp
//                    )
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun AnimatedSendButton(
//    onSendClick: () -> Unit,
//    onStopClick: () -> Unit,
//    enabled: Boolean,
//    modifier: Modifier = Modifier,
//    isLoading: Boolean,
//    isInputEmpty: Boolean
//) {
//    val transition = updateTransition(targetState = isLoading, label = "send_stop_transition")
//    val iconRotation by transition.animateFloat(
//        transitionSpec = { tween(300, easing = FastOutSlowInEasing) },
//        label = "icon_rotation"
//    ) { loading -> if (loading) 45f else 0f }
//    val rippleScale by animateFloatAsState(
//        targetValue = if (enabled || isLoading) 1.2f else 1f,
//        animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f),
//        label = "ripple_scale"
//    )
//    val colorAlpha by animateFloatAsState(
//        targetValue = if (isInputEmpty && !isLoading) 0.3f else 1f,
//        animationSpec = tween(300, easing = FastOutSlowInEasing),
//        label = "color_alpha"
//    )
//     val maxLines = 4
//    val hapticFeedback = LocalHapticFeedback.current
//
//    Box(
//        modifier = modifier
//            .clip(CircleShape)
//            .background(
//                brush = when {
//                    isLoading -> StopButtonGradient
//                    isInputEmpty -> InactiveSendButtonGradient
//                    else -> SendButtonGradient
//                },
//                alpha = colorAlpha
//            )
//            .drawWithContent {
//                drawContent()
//                drawCircle(
//                    color = Color.White.copy(alpha = 0.2f * (1 - rippleScale + 1f)),
//                    radius = size.minDimension * rippleScale / 2,
//                    style = Fill
//                )
//            }
//            .clickable(
//                enabled = enabled || isLoading,
//                onClick = {
//                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                    if (isLoading) onStopClick() else onSendClick()
//                }
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Send,
//            contentDescription = if (isLoading) "Stop Processing" else "Send",
//            tint = Color.White.copy(alpha = colorAlpha),
//            modifier = Modifier
//                .size(18.dp)
//                .graphicsLayer { rotationZ = iconRotation }
//        )
//    }
//}
//
//@Composable
//fun ThinkingIndicator(
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .wrapContentWidth(Alignment.Start)
//    ) {
//        Surface(
//            modifier = Modifier
//                .widthIn(max = 250.dp)
//                .shadow(4.dp, RoundedCornerShape(14.dp))
//                .drawWithContent {
//                    drawContent()
//                    drawRect(
//                        color = MessageGlow,
//                        style = Fill,
//                        alpha = 0.2f
//                    )
//                },
//            shape = RoundedCornerShape(14.dp),
//            color = Color.Transparent
//        ) {
//            Box(
//                modifier = Modifier
//                    .background(InputFieldGradient)
//                    .padding(10.dp)
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(6.dp)
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(14.dp),
//                        color = Color.White,
//                        strokeWidth = 1.5.dp
//                    )
//                    Text(
//                        text = "AI is thinking...",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = Color.White,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//            }
//        }
//    }
//}
//
//class ReverseRoundedShape : Shape {
//    override fun createOutline(
//        size: androidx.compose.ui.geometry.Size,
//        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
//        density: Density
//    ): Outline {
//        val cornerRadius = CornerRadius(12f * density.density)
//        val path = Path().apply {
//            addRoundRect(
//                RoundRect(
//                    rect = Rect(0f, 0f, size.width, size.height + cornerRadius.y),
//                    topLeft = CornerRadius.Zero,
//                    topRight = CornerRadius.Zero,
//                    bottomLeft = cornerRadius,
//                    bottomRight = cornerRadius
//                )
//            )
//        }
//        return Outline.Generic(path)
//    }
//}
