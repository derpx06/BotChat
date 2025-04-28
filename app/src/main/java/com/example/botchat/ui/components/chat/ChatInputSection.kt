package com.example.botchat.ui.components.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
        color = Transparent
    ) {
        Box(
            modifier = Modifier
                .background(if (isDarkTheme) InputFieldGradientDark else InputFieldGradientLight)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp),
                    label = { Text("Message AI Assistant...", style = MaterialTheme.typography.bodySmall.copy(color = if (isDarkTheme) GalacticGray else Black)) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = if (isDarkTheme) StarlightWhite else Black),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        onSendClick()
                        keyboardController?.hide()
                    }),
                    shape = RoundedCornerShape(18.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        focusedIndicatorColor = if (isDarkTheme) NeonCyan else Purple40,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = if (isDarkTheme) StarlightWhite else Black
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
    isDarkTheme: Boolean
) {
    val colorAlpha by animateFloatAsState(targetValue = if (isInputEmpty && !isLoading) 0.3f else 1f)

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                brush = when {
                    isLoading -> if (isDarkTheme) StopButtonGradientDark else StopButtonGradientLight
                    isInputEmpty -> if (isDarkTheme) InactiveSendButtonGradientDark else InactiveSendButtonGradientLight
                    else -> if (isDarkTheme) SendButtonGradientDark else SendButtonGradientLight
                },
                alpha = colorAlpha
            )
            .clickable(enabled = enabled || isLoading) {
                if (isLoading) onStopClick() else onSendClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Send,
            contentDescription = if (isLoading) "Stop" else "Send",
            tint = if (isDarkTheme) StarlightWhite else Black.copy(alpha = colorAlpha),
            modifier = Modifier.size(18.dp)
        )
    }
}