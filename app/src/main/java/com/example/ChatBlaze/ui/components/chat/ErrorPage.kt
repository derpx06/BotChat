package com.example.ChatBlaze.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.ui.theme.*
@Preview
@Composable
fun ErrorPage(
    isDarkTheme: Boolean=true,
    onDismiss: () -> Unit={}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) MidnightBlack else CloudWhite),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        color = if (isDarkTheme) PureWhite else SlateBlack
                    )
                )
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                        contentColor = if (isDarkTheme) MidnightBlack else PureWhite
                    )
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}