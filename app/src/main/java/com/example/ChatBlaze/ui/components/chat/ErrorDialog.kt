package com.example.ChatBlaze.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ChatBlaze.ui.theme.*

@Composable
fun ErrorDialog(
    errorMessage: String,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) MidnightBlack else CloudWhite
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (onRetry != null) {
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                                    contentColor = if (isDarkTheme) MidnightBlack else PureWhite
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                        OutlinedButton(
                            onClick = onDismiss,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (isDarkTheme) {
                                    Brush.linearGradient(listOf(ElectricCyan, ElectricCyan.copy(alpha = 0.5f)))
                                } else {
                                    Brush.linearGradient(listOf(Purple40, Purple40.copy(alpha = 0.5f)))
                                }
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}