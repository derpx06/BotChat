package com.example.botchat.ui.components.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.R
import com.example.botchat.ui.theme.*

@Composable
fun TopBar(
    onSettingsClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GalaxyRotation"
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GalaxyDrift"
    )
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(600)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(600))
    ) {
        val topBarShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(2.dp, topBarShape, ambientColor = ElectricCyan.copy(alpha = 0.2f))
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.1f),
                                Transparent,
                                PureWhite.copy(alpha = 0.1f)
                            ),
                            start = Offset(shimmerOffset * size.width, 0f),
                            end = Offset((shimmerOffset + 1f) * size.width, 0f)
                        ),
                        alpha = 0.3f
                    )
                    drawRect(
                        brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                        topLeft = Offset(0f, size.height - 3.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width, 3.dp.toPx())
                    )
                },
            shape = topBarShape,
            color = if (isDarkTheme) MidnightBlack else CloudWhite
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .offset(x = 2.dp)
                            .rotate(rotation)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Settings",
                            tint = if (isDarkTheme) ElectricCyan else Purple40,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}