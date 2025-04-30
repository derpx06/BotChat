package com.example.botchat.ui.components.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black.copy(alpha = 0.6f))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = if (isDarkTheme) StarlitPurple.copy(alpha = 0.95f) else SoftGray.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                color = if (isDarkTheme) ErrorRed else SlateBlack
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = if (isDarkTheme) PureWhite else SlateBlack
                            )
                        )
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (isDarkTheme) ElectricCyan else Purple40
                            )
                        )
                    }
                }
            }
        }
    }
}