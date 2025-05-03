package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.R
import com.example.botchat.ui.theme.*

@Composable
fun ErrorPage(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) MidnightBlack else CloudWhite)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.huggingface_logo),
                    contentDescription = "Hugging Face Logo",
                    modifier = Modifier.size(96.dp)
                )
                Text(
                    text = "503",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) PureWhite else SlateBlack
                    )
                )
                Text(
                    text = "Service Unavailable",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = if (isDarkTheme) GalacticGray else SlateBlack.copy(alpha = 0.7f)
                    )
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) ElectricCyan else Purple40,
                        contentColor = if (isDarkTheme) SlateBlack else PureWhite
                    )
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}