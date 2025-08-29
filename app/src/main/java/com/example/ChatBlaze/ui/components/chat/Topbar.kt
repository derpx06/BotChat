package com.example.ChatBlaze.ui.components.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatBlaze.ui.theme.*

@Preview
@Composable
fun TopBar(
    title: String = "AI Assistant",
    onMenuClick: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onSeetingsClicked:()->Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TopBarEffects")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )

    val menuInteractionSource = remember { MutableInteractionSource() }
    val isMenuPressed by menuInteractionSource.collectIsPressedAsState()
    val menuScale by animateFloatAsState(
        targetValue = if (isMenuPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "MenuScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Reduced height
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                ambientColor = if (isDarkTheme) NeonBlue.copy(alpha = 0.3f) else Aquamarine.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        color = if (isDarkTheme) MidnightBlack else CloudWhite
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isDarkTheme)
                            listOf(MidnightBlack, NeonBlue.copy(alpha = 0.1f))
                        else
                            listOf(CloudWhite, Aquamarine.copy(alpha = 0.1f)),
                        start = Offset(0f, 0f),
                        end = Offset(shimmerOffset * 1000f, 1000f)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp), // Reduced horizontal padding
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(44.dp) // Reduced button size
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isDarkTheme)
                                    listOf(NeonBlue.copy(alpha = 0.2f), Transparent)
                                else
                                    listOf(Aquamarine.copy(alpha = 0.2f), Transparent)
                            )
                        )
                        .scale(menuScale),
                    interactionSource = menuInteractionSource
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Menu",
                        tint = if (isDarkTheme) NeonBlue else Aquamarine,
                        modifier = Modifier.size(28.dp) // Reduced icon size
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isDarkTheme) PureWhite else SlateBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                IconButton(onClick = onSeetingsClicked) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Customize Model Response",
                        tint = if (isDarkTheme) NeonBlue else Aquamarine
                    )
                }

            }
        }
    }
}
