package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    // State to control visibility and trigger animations
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f)
                ) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(300)
                ),
        exit = fadeOut(animationSpec = tween(200)) +
                scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(200)
                ) +
                slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(200)
                ),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black.copy(alpha = 0.6f))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(
                        if (isDarkTheme) StarlitPurple.copy(alpha = 0.98f)
                        else SoftGray.copy(alpha = 0.98f)
                    ),
                color = Color.Transparent // Use background for color
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated error icon
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Error Icon",
                        tint = if (isDarkTheme) ErrorRed else SlateBlack,
                        modifier = Modifier
                            .size(48.dp)
                            .animateContentSize(
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                            )
                    )

                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            color = if (isDarkTheme) PureWhite else SlateBlack,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            color = if (isDarkTheme) PureWhite.copy(alpha = 0.9f) else SlateBlack.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            isVisible = false // Trigger exit animation
                            onDismiss() // Call dismiss callback
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                            contentColor = if (isDarkTheme) SlateBlack else PureWhite
                        )
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun ErrorDialogLightPreview() {
    MaterialTheme {
        ErrorDialog(
            errorMessage = "Failed to connect to the server. Please check your network and try again.",
            onDismiss = {},
            isDarkTheme = false
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun ErrorDialogDarkPreview() {
    MaterialTheme {
        ErrorDialog(
            errorMessage = "Failed to connect to the server. Please check your network and try again.",
            onDismiss = {},
            isDarkTheme = true
        )
    }
}