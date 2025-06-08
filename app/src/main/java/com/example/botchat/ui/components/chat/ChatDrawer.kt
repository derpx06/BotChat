package com.example.botchat.ui.components.chat

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.database.ChatDao
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
                targetValue = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                initialVelocity = velocity
            )
            drawerState = DrawerState.Closed
        }

        suspend fun openDrawer(velocity: Float = 0f) {
            translationX.animateTo(
                targetValue = drawerWidth,
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
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val targetOffsetX = decay.calculateTargetValue(translationX.value, velocity)
                        coroutineScope.launch {
                            val actualTargetX = if (targetOffsetX > drawerWidth * 0.5f) drawerWidth else 0f
                            translationX.animateTo(
                                targetValue = actualTargetX,
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
                modelDao = modelDao as modelDao
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
    Surface(
        modifier = modifier
            .width(DrawerWidth)
            .fillMaxHeight()
            .shadow(8.dp, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)),
        color = if (isDarkTheme) Color(0xFF1F1F1F) else Color(0xFFF7F7F7),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Conversations",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) PureWhite else SlateBlack,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val sessions by chatViewModel.allSessions.collectAsStateWithLifecycle(initialValue = emptyList())
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = sessions,
                    key = { it.sessionId }
                ) { session ->
                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    chatViewModel.deleteSession(session.sessionId)
                                    true
                                } else {
                                    false
                                }
                            }
                        ),
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF44336))
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                        },
                        content = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (session.sessionId == chatViewModel.uiState.value.currentSessionId)
                                        if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
                                    else if (isDarkTheme) Color(0xFF1F1F1F) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            chatViewModel.loadSession(session.sessionId)
                                            onScreenSelected(Screen.Chat)
                                        }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = session.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isDarkTheme) PureWhite else SlateBlack,
                                            fontSize = 16.sp
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            chatViewModel.deleteSession(session.sessionId)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Session",
                                            tint = if (isDarkTheme) Color(0xFFB0BEC5) else Color(0xFF607D8B)
                                        )
                                    }
                                }
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        chatViewModel.clearMessages()
                        onScreenSelected(Screen.ClearChat)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Clear All History", style = MaterialTheme.typography.labelMedium)
                }
                Row {
                    IconButton(onClick = {
                        chatViewModel.startNewChat()
                        onScreenSelected(Screen.Chat)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = if (isDarkTheme) PrimaryBlue else Aquamarine
                        )
                    }
                    IconButton(onClick = { onScreenSelected(Screen.Settings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isDarkTheme) PrimaryBlue else Aquamarine
                        )
                    }
                    IconButton(onClick = { onScreenSelected(Screen.Models) }) {
                        Icon(
                            imageVector = Icons.Default.Settings, // TODO: Replace with appropriate Models icon
                            contentDescription = "Models",
                            tint = if (isDarkTheme) PrimaryBlue else Aquamarine
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

private enum class Screen {
    Chat,
    Settings,
    Models,
    ClearChat
}

private val DrawerWidth = 280.dp