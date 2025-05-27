package com.example.botchat.ui.components.chat

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import com.example.botchat.database.modelDatabase.modelDao
import com.example.botchat.ui.theme.*
import com.example.botchat.viewmodel.Chat.ChatViewModel
import com.example.botchat.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@SuppressLint("RestrictedApi")
@Composable
fun ChatDrawer(
    chatViewModel: ChatViewModel,
    settingViewModel: SettingViewModel,
    onNavigateToModels: () -> Unit,
    modelDao: modelDao,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = if (isDarkTheme) MidnightBlack else CloudWhite
    ) {
        var drawerState by remember { mutableStateOf(DrawerState.Closed) }
        val translationX = remember { Animatable(0f) }
        val drawerWidth = with(LocalDensity.current) { DrawerWidth.toPx() }
        translationX.updateBounds(0f, drawerWidth)
        val coroutineScope = rememberCoroutineScope()

        suspend fun closeDrawer(velocity: Float = 0f) {
            translationX.animateTo(
                0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                initialVelocity = velocity
            )
            drawerState = DrawerState.Closed
        }

        suspend fun openDrawer(velocity: Float = 0f) {
            translationX.animateTo(
                drawerWidth,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                initialVelocity = velocity
            )
            drawerState = DrawerState.Open
        }

        fun toggleDrawer() {
            coroutineScope.launch {
                if (drawerState == DrawerState.Open) closeDrawer() else openDrawer()
            }
        }

        val velocityTracker = remember { VelocityTracker() }
        PredictiveBackHandler(drawerState == DrawerState.Open) { progress ->
            try {
                progress.collect { backEvent ->
                    val targetSize = drawerWidth - (drawerWidth * backEvent.progress)
                    translationX.snapTo(targetSize)
                    velocityTracker.addPosition(
                        SystemClock.uptimeMillis(),
                        Offset(backEvent.touchX, backEvent.touchY)
                    )
                }
                closeDrawer(velocityTracker.calculateVelocity().x)
            } catch (e: CancellationException) {
                openDrawer(velocityTracker.calculateVelocity().x)
            }
            velocityTracker.resetTracking()
        }

        AnimatedVisibility(
            visible = drawerState == DrawerState.Open,
            enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(dampingRatio = 0.6f)
            ),
            exit = fadeOut(animationSpec = tween(400)) + slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(dampingRatio = 0.6f)
            )
        ) {
            ChatDrawerContents(
                onScreenSelected = { screen ->
                    when (screen) {
                        Screen.Settings -> settingViewModel.toggleSettings()
                        Screen.Models -> onNavigateToModels()
                        Screen.ClearChat -> chatViewModel.clearMessages()
                        Screen.Chat -> {}
                    }
                    coroutineScope.launch { closeDrawer() }
                },
                isDarkTheme = isDarkTheme
            )
        }

        val draggableState = rememberDraggableState { dragAmount ->
            coroutineScope.launch { translationX.snapTo(translationX.value + dragAmount) }
        }
        val decay = rememberSplineBasedDecay<Float>()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale = lerp(1f, 0.92f, translationX.value / drawerWidth)
                    scaleX = scale
                    scaleY = scale
                    shape = RoundedCornerShape(lerp(0f, 28.dp.toPx(), translationX.value / drawerWidth))
                    clip = true
                    shadowElevation = 20f
                }
                .draggable(
                    draggableState,
                    Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val targetOffsetX = decay.calculateTargetValue(translationX.value, velocity)
                        coroutineScope.launch {
                            val actualTargetX = if (targetOffsetX > drawerWidth * 0.5) drawerWidth else 0f
                            translationX.animateTo(
                                actualTargetX,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                                initialVelocity = velocity
                            )
                            drawerState = if (actualTargetX == drawerWidth) DrawerState.Open else DrawerState.Closed
                        }
                    }
                )
        ) {
            ChatScreenContent(
                chatViewModel = chatViewModel,
                settingViewModel = settingViewModel,
                onNavigateToModels = onNavigateToModels,
                onDrawerClicked = ::toggleDrawer,
                modelDao = modelDao
            )
        }
    }
}

@Composable
private fun ChatDrawerContents(
    onScreenSelected: (Screen) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(DrawerWidth)
            .fillMaxHeight()
            .shadow(6.dp, RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)),
        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) MidnightBlack.copy(alpha = 0.98f) else CloudWhite.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDarkTheme)
                            listOf(MidnightBlack.copy(alpha = 0.98f), NeonBlue.copy(alpha = 0.05f))
                        else
                            listOf(CloudWhite.copy(alpha = 0.98f), Aquamarine.copy(alpha = 0.05f))
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = if (isDarkTheme) NeonBlue else Aquamarine,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Screen.entries.forEach { screen ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            screen.text,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isDarkTheme) PureWhite else SlateBlack,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        )
                    },
                    selected = false,
                    onClick = { onScreenSelected(screen) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = if (isDarkTheme)
                                    listOf(NeonBlue.copy(alpha = 0.15f), Transparent)
                                else
                                    listOf(Aquamarine.copy(alpha = 0.15f), Transparent)
                            )
                        )
                        .padding(vertical = 8.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Transparent,
                        unselectedTextColor = if (isDarkTheme) PureWhite else SlateBlack,
                        unselectedIconColor = if (isDarkTheme) NeonBlue else Aquamarine
                    )
                )
            }
        }
    }
}

private enum class DrawerState {
    Open,
    Closed
}

private enum class Screen(val text: String) {
    Chat("Chat"),
    Settings("Settings"),
    Models("Models"),
    ClearChat("Clear Chat")
}

private val DrawerWidth = 300.dp