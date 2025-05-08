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
    isDarkTheme: Boolean,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight)
            .border(
                width = 1.dp,
                brush = if (isDarkTheme) Brush.linearGradient(
                    listOf(ElectricCyan.copy(alpha = 0.3f), Transparent)
                ) else Brush.linearGradient(
                    listOf(Purple40.copy(alpha = 0.2f), Transparent)
                ),
                shape = RoundedCornerShape(28.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = if (isDarkTheme) PureWhite else SlateBlack
            ),
            decorationBox = { innerTextField ->
                if (inputText.isEmpty()) {
                    Text(
                        text = "Type a message...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
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
                enabled = inputText.isNotBlank(),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.example.botchat.R.drawable.ic_send),
                    contentDescription = "Send",
                    tint = if (isDarkTheme) ElectricCyan else Purple40,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
        ) {
            IconButton(
                onClick = onStopClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = "Stop",
                    tint = if (isDarkTheme) ElectricCyan else Purple40,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}