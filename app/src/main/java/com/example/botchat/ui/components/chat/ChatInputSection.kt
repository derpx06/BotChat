package com.example.botchat.ui.components.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun ChatInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    isLoading: Boolean,
    isDarkTheme: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val isFocused = inputText.isNotEmpty() || isLoading
    val infiniteTransition = rememberInfiniteTransition(label = "BorderGlow")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BorderGlowAlpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(20.dp),
        color = Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight,
                    alpha = 0.6f
                )
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.5.dp,
                    brush = if (isDarkTheme) Brush.linearGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = borderAlpha),
                            ElectricCyan.copy(alpha = borderAlpha * 0.5f)
                        )
                    ) else Brush.linearGradient(
                        colors = listOf(
                            Purple40.copy(alpha = borderAlpha),
                            Purple40.copy(alpha = borderAlpha * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.15f),
                                Transparent
                            )
                        ),
                        alpha = 0.4f
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    label = {
                        Text(
                            "Message AI Assistant...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDarkTheme) GalacticGray else SlateBlack.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        onSendClick()
                        keyboardController?.hide()
                    }),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = if (isDarkTheme) ElectricCyan else Purple40
                    ),
                    maxLines = 4
                )
                AnimatedSendButton(
                    onSendClick = {
                        onSendClick()
                        keyboardController?.hide()
                    },
                    onStopClick = onStopClick,
                    enabled = inputText.isNotBlank() && !isLoading,
                    isLoading = isLoading,
                    isInputEmpty = inputText.isBlank(),
                    isFocused = isFocused,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
private fun AnimatedSendButton(
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    isInputEmpty: Boolean,
    isFocused: Boolean,
    isDarkTheme: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isFocused && !isLoading) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val colorAlpha by animateFloatAsState(
        targetValue = if (isInputEmpty && !isLoading) 0.4f else 1f,
        animationSpec = tween(300)
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val rippleScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 1.5f else 1f,
        animationSpec = tween(200)
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(pulseScale * rippleScale)
            .clip(CircleShape)
            .background(
                brush = when {
                    isLoading -> if (isDarkTheme) StopButtonGradientDark else StopButtonGradientLight
                    isInputEmpty -> if (isDarkTheme) InactiveSendButtonGradientDark else InactiveSendButtonGradientLight
                    else -> if (isDarkTheme) SendButtonGradientDark else SendButtonGradientLight
                },
                alpha = colorAlpha
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled || isLoading
            ) {
                if (isLoading) onStopClick() else onSendClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Send,
            contentDescription = if (isLoading) "Stop" else "Send",
            tint = if (isDarkTheme) PureWhite else SlateBlack.copy(alpha = colorAlpha),
            modifier = Modifier.size(20.dp)
        )
    }
}