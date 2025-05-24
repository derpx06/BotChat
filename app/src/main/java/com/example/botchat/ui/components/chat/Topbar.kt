package com.example.botchat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
    onDeleteClick: () -> Unit,
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

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = spring(dampingRatio = 0.7f)
        ),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    ambientColor = ElectricCyan.copy(alpha = 0.2f)
                )
                .drawWithContent {
                    drawContent()
                    // Shimmer effect
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.08f),
                                Transparent,
                                PureWhite.copy(alpha = 0.08f)
                            ),
                            start = Offset(shimmerOffset * size.width, 0f),
                            end = Offset((shimmerOffset + 1f) * size.width, 0f)
                        ),
                        alpha = 0.25f
                    )
                },
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            color = if (isDarkTheme) MidnightBlack else CloudWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.3.sp
                    ),
                    modifier = Modifier.padding(start = PaddingSmall)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val settingsInteractionSource = remember { MutableInteractionSource() }
                    val isSettingsHovered by settingsInteractionSource.collectIsHoveredAsState()

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                alpha = 0.9f
                            )
                            .border(
                                width = 0.5.dp,
                                brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .rotate(rotation)
                            .scale(if (isSettingsHovered) 1.05f else 1f),
                        interactionSource = settingsInteractionSource
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Settings",
                          //  tint = if (isDarkTheme) ElectricCyan else Purple40,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    val modelsInteractionSource = remember { MutableInteractionSource() }
                    val isModelsHovered by modelsInteractionSource.collectIsHoveredAsState()

                    IconButton(
                        onClick = onModelsClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                alpha = 0.9f
                            )
                            .border(
                                width = 0.5.dp,
                                brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .offset(y = drift.dp)
                            .scale(if (isModelsHovered) 1.05f else 1f),
                        interactionSource = modelsInteractionSource
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_list),
                            contentDescription = "Models",
                            tint = if (isDarkTheme) ElectricCyan else Purple40,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    val deleteInteractionSource = remember { MutableInteractionSource() }
                    val isDeleteHovered by deleteInteractionSource.collectIsHoveredAsState()

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = if (isDarkTheme) ResponseGradientDarkMode else ResponseGradientLightMode,
                                alpha = 0.9f
                            )
                            .border(
                                width = 0.5.dp,
                                brush = if (isDarkTheme) TopBarUnderlineDark else TopBarUnderlineLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .offset(y = drift.dp)
                            .scale(if (isDeleteHovered) 1.05f else 1f),
                        interactionSource = deleteInteractionSource
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "Clear Chat",
                            tint = if (isDarkTheme) ElectricCyan else Purple40,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// Padding Constants
private val PaddingSmall = 8.dp
private val PaddingMedium = 12.dp