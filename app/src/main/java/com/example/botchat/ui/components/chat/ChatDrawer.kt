package com.example.botchat.ui.components.chat

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                chatViewModel = chatViewModel,
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ChatDrawerContents(
    chatViewModel: ChatViewModel,
    onScreenSelected: (Screen) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val sessions by chatViewModel.allSessions.collectAsStateWithLifecycle(initialValue = emptyList())
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
                .fillMaxHeight()
                .padding(20.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDarkTheme)
                            listOf(MidnightBlack.copy(alpha = 0.98f), NeonBlue.copy(alpha = 0.05f))
                        else
                            listOf(CloudWhite.copy(alpha = 0.98f), Aquamarine.copy(alpha = 0.05f))
                    )
                ),
            verticalArrangement = Arrangement.SpaceBetween
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

            // Session List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDarkTheme) PureWhite else SlateBlack,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (session.sessionId == chatViewModel.uiState.value.currentSessionId)
                                    if (isDarkTheme) NeonBlue.copy(alpha = 0.3f) else Aquamarine.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .padding(12.dp)
                            .clickable {
                                chatViewModel.loadSession(session.sessionId)
                                onScreenSelected(Screen.Chat)
                            }
                    )
                }
            }

            // New Chat and Icon Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        chatViewModel.startNewChat()
                        onScreenSelected(Screen.Chat)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) NeonBlue else Aquamarine
                    )
                ) {
                    Text("New Chat", color = if (isDarkTheme) MidnightBlack else CloudWhite)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { onScreenSelected(Screen.Settings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isDarkTheme) NeonBlue else Aquamarine
                        )
                    }
                    IconButton(onClick = { onScreenSelected(Screen.Models) }) {
                        Icon(
                            imageVector = Icons.Default.Settings, // Placeholder; replace with Models icon if available
                            contentDescription = "Models",
                            tint = if (isDarkTheme) NeonBlue else Aquamarine
                        )
                    }
                    IconButton(onClick = { onScreenSelected(Screen.ClearChat) }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Chat",
                            tint = if (isDarkTheme) NeonBlue else Aquamarine
                        )
                    }
                }
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