package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDarkTheme) MidnightBlack else CloudWhite)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight)
                .border(
                    width = 1.dp,
                    brush = if (isDarkTheme) Brush.linearGradient(
                        listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
                    ) else Brush.linearGradient(
                        listOf(Purple40.copy(alpha = 0.2f), Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack
                ),
                decorationBox = { innerTextField ->
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Type a message...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                color = if (isDarkTheme) PureWhite.copy(alpha = 0.5f) else SlateBlack.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            )

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
            ) {
                IconButton(
                    onClick = onSendClick,
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        painter = painterResource(id = com.example.botchat.R.drawable.ic_send),
                        contentDescription = "Send",
                        tint = if (isDarkTheme) ElectricCyan else Purple40
                    )
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
            ) {
                IconButton(onClick = onStopClick) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Stop",
                        tint = if (isDarkTheme) ElectricCyan else Purple40
                    )
                }
            }
        }
    }
}