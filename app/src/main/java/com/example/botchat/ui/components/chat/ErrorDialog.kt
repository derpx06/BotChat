package com.example.botchat.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) StarlitPurple else MistGray),
        title = {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack
                )
            )
        },
        text = {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = if (isDarkTheme) PureWhite else SlateBlack
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDarkTheme) ElectricCyan else Purple40
                )
            ) {
                Text("OK")
            }
        }
    )
}