package com.example.ChatBlaze.ui.components.chat


import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.ui.theme.BackgroundGradientDark
import com.example.ChatBlaze.ui.theme.PowderBlue
import com.example.ChatBlaze.ui.theme.SkyBlue
import com.example.ChatBlaze.ui.theme.White

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    isLoading: Boolean,
    isDarkTheme: Boolean = false,
    useGradientTheme: Boolean = true,
    modifier: Modifier = Modifier,
    photo_supported: Boolean = false
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
    val textFieldBackgroundColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF0F2F5)
    val iconColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF5F6368)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color(0xFF9E9E9E) else Color(0xFF8A8A8A)
    val sendButtonActiveColor = Color(0xFF2A9D8F)
    val sendButtonInactiveColor = if (isDarkTheme) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)

    val sendEnabled = inputText.isNotBlank() && !isLoading

    val surfaceBrush = if (useGradientTheme) {
        if (isDarkTheme)
            Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF2C2C2E)))
        else
            Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0F2F5)))
    } else Brush.linearGradient(listOf(backgroundColor, backgroundColor))

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 1.dp, vertical = 15.dp),
        shape = RoundedCornerShape(27.dp),
        color = Color.Transparent
       // shadowElevation = 20.dp
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.2f)),
        ) {
            Row(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 5.dp) ,
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(textFieldBackgroundColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Attachment",
                        tint = iconColor.copy(alpha = 01f)
                    )
                }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(textFieldBackgroundColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = inputText,
                                onValueChange = onInputChange,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp)
                                    .heightIn(min = 25.dp, max = 120.dp),
                                textStyle = TextStyle(
                                    color = textColor,
                                    fontSize = 17.sp
                                ),
                                cursorBrush = SolidColor(sendButtonActiveColor),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (inputText.isEmpty()) {
                                            Text(
                                                "Enter your message…",
                                                color = placeholderColor,
                                                fontSize = 17.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            AnimatedVisibility(
                                visible = inputText.isNotEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Text",
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDarkTheme) Color.Gray.copy(alpha = 0.3f) else Color.Gray.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        .padding(2.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = { onInputChange("") }
                                        )
                                )
                            }
                        }
                    }
                Column {
                    IconButton(
                        onClick = if (isLoading) onStopClick else onSendClick,
                        enabled = sendEnabled || isLoading,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    if (sendEnabled || isLoading)
                                        listOf(
                                            sendButtonActiveColor,
                                            sendButtonActiveColor.copy(alpha = 0.85f)
                                        )
                                    else
                                        listOf(sendButtonInactiveColor, sendButtonInactiveColor)
                                )
                            )
                    ) {
                        AnimatedContent(
                            targetState = Pair(isLoading, inputText.isNotBlank()),
                            transitionSpec = {
                                (slideInVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) { it } + fadeIn() with
                                        slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) { -it } + fadeOut())
                                    .using(SizeTransform(clip = false))
                            },
                            label = "SendButtonAnimation"
                        ) { (loading, hasText) ->
                            when {
                                loading -> CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )

                                hasText -> Icon(
                                    imageVector = Icons.Rounded.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.offset(x = (-2).dp)
                                )

                                else -> Icon(
                                    imageVector = Icons.Rounded.Send,
                                    contentDescription = "Send",
                                    tint = if (isDarkTheme) Color(0xFF6E6E72) else Color(0xFFBDBDC2)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- Previews for easy visualization ---

@Preview(name = "Chat Input - Light Theme - Empty")
@Composable
private fun ChatInputLightEmptyPreview() {
    ChatInputSection(
        inputText = "",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        isLoading = false,
        isDarkTheme = false
    )
}

@Preview(name = "Chat Input - Light Theme - With Text")
@Composable
private fun ChatInputLightWithTextPreview() {
    ChatInputSection(
        inputText = "Hello, how are you?",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        isLoading = false,
        isDarkTheme = false
    )
}

@Preview(name = "Chat Input - Dark Theme - With Text")
@Composable
private fun ChatInputDarkWithTextPreview() {
    ChatInputSection(
        inputText = "Hello, how are you?",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        isLoading = false,
        isDarkTheme = true
    )
}

@Preview(name = "Chat Input - Dark Theme - Loading")
@Composable
private fun ChatInputDarkLoadingPreview() {
    ChatInputSection(
        inputText = "Tell me a story about a dragon",
        onInputChange = {},
        onSendClick = {},
        onStopClick = {},
        isLoading = true,
        isDarkTheme = true
    )
}