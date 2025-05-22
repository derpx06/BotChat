package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

// Padding Constants (Aligned with ChatMessages.kt)
private val PaddingTiny = 4.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp

@Composable
fun ChatInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    isLoading: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                alpha = 0.92f
            )
            .border(
                width = 0.75.dp,
                color = if (isDarkTheme) GalacticGray.copy(alpha = 0.6f) else CoolGray.copy(alpha = 0.6f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 120.dp)
                    .padding(end = PaddingSmall),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(if (isDarkTheme) ElectricCyan else Purple40),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Enter your message…",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        color = if (isDarkTheme) PureWhite.copy(alpha = 0.5f) else SlateBlack.copy(alpha = 0.5f),
                                        lineHeight = 24.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                        AnimatedVisibility(
                            visible = inputText.isNotEmpty(),
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            IconButton(
                                onClick = { onInputChange("") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = if (isDarkTheme) GalacticGray else CoolGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            )

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f, animationSpec = spring(dampingRatio = 0.8f)),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()

                IconButton(
                    onClick = onSendClick,
                    enabled = inputText.isNotBlank(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = if (inputText.isNotBlank()) {
                                if (isDarkTheme) SendButtonGradientDark else SendButtonGradientLight
                            } else {
                                if (isDarkTheme) InactiveSendButtonGradientDark else InactiveSendButtonGradientLight
                            },
                            alpha = if (inputText.isNotBlank()) 1f else 0.6f
                        )
                        .scale(if (isHovered && inputText.isNotBlank()) 1.05f else 1f),
                    interactionSource = interactionSource
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (isDarkTheme) PureWhite else SlateBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f, animationSpec = spring(dampingRatio = 0.8f)),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()

                IconButton(
                    onClick = onStopClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = if (isDarkTheme) StopButtonGradientDark else StopButtonGradientLight,
                            alpha = 1f
                        )
                        .scale(if (isHovered) 1.05f else 1f),
                    interactionSource = interactionSource
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop",
                        tint = if (isDarkTheme) PureWhite else SlateBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}