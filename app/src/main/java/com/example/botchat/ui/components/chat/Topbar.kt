package com.example.botchat.ui.components.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.R
import com.example.botchat.ui.theme.*

@Composable
fun TopBar(onSettingsClick: () -> Unit, isDarkTheme: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(StarlightWhite.copy(alpha = 0.1f), StarlightWhite.copy(alpha = 0.05f))
                    ),
                    alpha = 0.5f
                )
                drawRect(
                    brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                    topLeft = Offset(0f, size.height - 2f),
                    size = androidx.compose.ui.geometry.Size(size.width, 2f)
                )
            },
        shape = ReverseRoundedShape(),
        color = if (isDarkTheme) CosmicPurple.copy(alpha = 0.9f) else LightSecondary.copy(alpha = 0.8f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(ReverseRoundedShape())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Assistant",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isDarkTheme) StarlightWhite else Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                val infiniteTransition = rememberInfiniteTransition()
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(8000, easing = LinearEasing)
                    )
                )
                IconButton(onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Logo",
                        tint = if (isDarkTheme) NeonCyan else Black,
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer { rotationZ = angle }
                    )
                }

            }
        }
    }
}