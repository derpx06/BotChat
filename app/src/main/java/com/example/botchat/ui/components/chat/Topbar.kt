package com.example.botchat.ui.components

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String = "AI Assistant",
    onSettingsClick: () -> Unit,
    onModelsClick: () -> Unit = {},
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TopBarEffects")

    // Shimmer animation
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )

    // Settings icon rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SettingsRotation"
    )

    // Gentle floating effect
    val drift by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconDrift"
    )

    val topBarShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 4.dp,
                shape = topBarShape,
                ambientColor = ElectricCyan.copy(alpha = 0.2f)
            )
            .drawWithContent {
                drawContent()
                // Shimmer effect
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
                // Bottom border
//                drawRect(
//                    brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
//                    topLeft = Offset(0f, size.height - 2.dp.toPx()),
//                    size = androidx.compose.ui.geometry.Size(size.width, 2.dp.toPx())
//                )
            },
        shape = topBarShape,
        color = if (isDarkTheme) MidnightBlack else CloudWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(start = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onModelsClick,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = drift.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_list),
                        contentDescription = "Models",
                        tint = if (isDarkTheme) ElectricCyan else Purple40,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(rotation)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Settings",
                        tint = if (isDarkTheme) ElectricCyan else Purple40,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}