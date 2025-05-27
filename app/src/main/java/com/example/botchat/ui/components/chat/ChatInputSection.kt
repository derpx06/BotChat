package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Padding Constants
private val PaddingTiny = 4.dp
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp
private val PaddingLarge = 16.dp
@Composable
fun ChatInputSection(
    inputText: String="",
    onInputChange: (String) -> Unit={},
    onSendClick: () -> Unit={},
    onStopClick: () -> Unit={},
    isLoading: Boolean =false,
    isDarkTheme: Boolean,
    useGradientTheme: Boolean = false,
    modifier: Modifier = Modifier,
    photo_supported: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Effects")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorAlpha"
    )
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BorderAlpha"
    )
    val cardInteractionSource = remember { MutableInteractionSource() }
    val isPressed by cardInteractionSource.collectIsPressedAsState()
    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 4.dp,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "CardElevation"
    )

    // Colors
    val backgroundColor = if (isDarkTheme) Color(0xFF1C2526) else Color(0xFFF5F7FA)
    val borderColor = if (isDarkTheme) Color(0xFF4A5A5B) else Color(0xFFD1D5DB)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val buttonEnabledColor = Color(0xFF2A9D8F)
    val buttonDisabledColor = borderColor
    val backgroundBrush = if (useGradientTheme) {
        if (isDarkTheme)
            Brush.linearGradient(listOf(Color(0xFF2A3B4C), Color(0xFF1C2526)))
        else
            Brush.linearGradient(listOf(Color(0xFFE6ECEF), Color(0xFFF5F7FA)))
    } else {
        Brush.linearGradient(listOf(backgroundColor, backgroundColor))
    }
    val buttonEnabledBrush = if (useGradientTheme) backgroundBrush else Brush.linearGradient(listOf(buttonEnabledColor, buttonEnabledColor))
    val buttonDisabledBrush = Brush.linearGradient(listOf(buttonDisabledColor, buttonDisabledColor))

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = spring(dampingRatio = 0.7f)
        ),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f)
    ) {
        Card(
            onClick = {}, // Empty lambda; elevation handled via interactionSource
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            interactionSource = cardInteractionSource
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .background(brush = backgroundBrush)
                    .border(
                        width = 0.75.dp,
                        brush = Brush.linearGradient(listOf(borderColor.copy(alpha = borderAlpha), borderColor.copy(alpha = borderAlpha))),
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(brush = backgroundBrush)
                            .border(
                                width = 0.5.dp,
                                brush = Brush.linearGradient(listOf(borderColor.copy(alpha = 0.85f), borderColor.copy(alpha = 0.85f))),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = PaddingMedium, vertical = PaddingSmall)
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 120.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = textColor,
                                lineHeight = 28.sp,
                                letterSpacing = 0.5.sp
                            ),
                            cursorBrush = SolidColor(if (isDarkTheme) buttonEnabledColor.copy(alpha = cursorAlpha) else buttonEnabledColor.copy(alpha = cursorAlpha)),
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
                                                    fontSize = 18.sp,
                                                    color = textColor.copy(alpha = 0.6f),
                                                    lineHeight = 28.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    letterSpacing = 0.5.sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                    AnimatedVisibility(
                                        visible = inputText.isNotEmpty(),
                                        enter = fadeIn(animationSpec = tween(150)),
                                        exit = fadeOut(animationSpec = tween(150))
                                    ) {
                                        IconButton(
                                            onClick = { onInputChange("") },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(brush = backgroundBrush)
                                                .border(
                                                    width = 0.4.dp,
                                                    brush = Brush.linearGradient(listOf(borderColor, borderColor)),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = borderColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(PaddingTiny))

                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = fadeIn(animationSpec = tween(150)) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = spring(dampingRatio = 0.7f)
                        ),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.9f)
                    ) {
                        val buttonInteractionSource = remember { MutableInteractionSource() }
                        val isHovered by buttonInteractionSource.collectIsHoveredAsState()
                        val buttonDrift by infiniteTransition.animateFloat(
                            initialValue = -1f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "ButtonDrift"
                        )

                        IconButton(
                            onClick = onSendClick,
                            enabled = inputText.isNotBlank(),
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = if (inputText.isNotBlank()) buttonEnabledBrush else buttonDisabledBrush,
                                    alpha = if (inputText.isNotBlank()) 1f else 0.6f
                                )
                                .border(
                                    width = 0.5.dp,
                                    brush = Brush.linearGradient(listOf(borderColor.copy(alpha = if (isHovered) 1f else 0.9f), borderColor.copy(alpha = if (isHovered) 1f else 0.9f))),
                                    shape = CircleShape
                                )
                                .offset(y = buttonDrift.dp)
                                .scale(if (isHovered && inputText.isNotBlank()) 1.15f else 1f),
                            interactionSource = buttonInteractionSource
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = textColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(animationSpec = tween(150)) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = spring(dampingRatio = 0.7f)
                        ),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.9f)
                    ) {
                        val buttonInteractionSource = remember { MutableInteractionSource() }
                        val isHovered by buttonInteractionSource.collectIsHoveredAsState()
                        val buttonDrift by infiniteTransition.animateFloat(
                            initialValue = -1f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "ButtonDrift"
                        )

                        IconButton(
                            onClick = onStopClick,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(brush = buttonEnabledBrush)
                                .border(
                                    width = 0.5.dp,
                                    brush = Brush.linearGradient(listOf(borderColor.copy(alpha = if (isHovered) 1f else 0.9f), borderColor.copy(alpha = if (isHovered) 1f else 0.9f))),
                                    shape = CircleShape
                                )
                                .offset(y = buttonDrift.dp)
                                .scale(if (isHovered) 1.15f else 1f),
                            interactionSource = buttonInteractionSource
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop",
                                tint = textColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}