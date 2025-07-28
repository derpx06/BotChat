package com.example.ChatBlaze.ui.components.chat

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ChatBlaze.database.modelDatabase.modelDao
import com.example.ChatBlaze.viewmodel.Chat.ChatViewModel
import com.example.ChatBlaze.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

// --- ENUMS AND CONSTANTS ---

private enum class DrawerState { Open, Closed }
private enum class Screen { Chat, Settings, Models, ClearChat }
private val DrawerWidth = 300.dp // Increased width for better spacing

@SuppressLint("RestrictedApi")
@Composable
fun ChatDrawer(
    chatViewModel: ChatViewModel,
    settingViewModel: SettingViewModel,
    onNavigateToModels: () -> Unit,
    modelDao: modelDao,
    modifier: Modifier = Modifier
) {
    // Use MaterialTheme colors for a consistent look
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        val drawerState = remember { mutableStateOf(DrawerState.Closed) }
        val translationX = remember { Animatable(0f) }
        val drawerWidthPx = with(LocalDensity.current) { DrawerWidth.toPx() }
        val coroutineScope = rememberCoroutineScope()

        translationX.updateBounds(0f, drawerWidthPx)

        // --- DRAWER ANIMATION LOGIC ---
        suspend fun closeDrawer(velocity: Float = 0f) {
            translationX.animateTo(
                targetValue = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                initialVelocity = velocity
            )
            drawerState.value = DrawerState.Closed
        }

        suspend fun openDrawer(velocity: Float = 0f) {
            translationX.animateTo(
                targetValue = drawerWidthPx,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                initialVelocity = velocity
            )
            drawerState.value = DrawerState.Open
        }

        fun toggleDrawer() {
            coroutineScope.launch {
                if (drawerState.value == DrawerState.Open) closeDrawer() else openDrawer()
            }
        }

        // --- PREDICTIVE BACK HANDLER ---
        val velocityTracker = remember { VelocityTracker() }
        PredictiveBackHandler(drawerState.value == DrawerState.Open) { progress ->
            try {
                progress.collect { backEvent ->
                    val targetSize = drawerWidthPx - (drawerWidthPx * backEvent.progress)
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


        // --- UI LAYOUT ---
        ChatDrawerContents(
            chatViewModel = chatViewModel,
            onScreenSelected = { screen ->
                when (screen) {
                    Screen.Settings -> settingViewModel.toggleSettings()
                    Screen.Models -> onNavigateToModels()
                    Screen.ClearChat -> chatViewModel.clearMessages()
                    Screen.Chat -> {} // Handled by item clicks
                }
                coroutineScope.launch { closeDrawer() }
            }
        )

        val draggableState = rememberDraggableState { dragAmount ->
            coroutineScope.launch { translationX.snapTo(translationX.value + dragAmount) }
        }
        val decay = rememberSplineBasedDecay<Float>()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale = lerp(1f, 0.9f, translationX.value / drawerWidthPx)
                    scaleX = scale
                    scaleY = scale
                    val cornerRadius = lerp(0f, 32.dp.toPx(), translationX.value / drawerWidthPx)
                    shape = RoundedCornerShape(cornerRadius)
                    clip = true
                    shadowElevation = 24f
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val targetOffsetX = decay.calculateTargetValue(translationX.value, velocity)
                        coroutineScope.launch {
                            val actualTargetX = if (targetOffsetX > drawerWidthPx * 0.5f) drawerWidthPx else 0f
                            translationX.animateTo(
                                targetValue = actualTargetX,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.6f),
                                initialVelocity = velocity
                            )
                            drawerState.value = if (actualTargetX == drawerWidthPx) DrawerState.Open else DrawerState.Closed
                        }
                    }
                )
        ) {
            // Added a placeholder for ChatScreenContent to resolve the error
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
    chatViewModel: ChatViewModel,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .width(DrawerWidth)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Text(
                text = "Conversations",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            // --- CONVERSATION LIST ---
            val sessions by chatViewModel.allSessions.collectAsStateWithLifecycle(initialValue = emptyList())
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = sessions,
                    key = { it.sessionId }
                ) { session ->
                    val isSelected = session.sessionId == uiState.currentSessionId
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    chatViewModel.loadSession(session.sessionId)
                                    onScreenSelected(Screen.Chat)
                                }
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 8.dp), // Adjust padding for button
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 16.dp)
                            )
                            // Replaced swipe-to-dismiss with a simple delete button
                            IconButton(onClick = { chatViewModel.deleteSession(session.sessionId) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Session",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // --- SPACER & DIVIDER ---
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // --- ACTION BUTTONS ---
            DrawerActionItem(
                label = "New Chat",
                icon = Icons.Outlined.AddComment,
                onClick = {
                    chatViewModel.startNewChat()
                    onScreenSelected(Screen.Chat)
                }
            )
            DrawerActionItem(
                label = "Models",
                icon = Icons.Outlined.Category,
                onClick = { onScreenSelected(Screen.Models) }
            )
            DrawerActionItem(
                label = "Settings",
                icon = Icons.Outlined.Settings,
                onClick = { onScreenSelected(Screen.Settings) }
            )
            DrawerActionItem(
                label = "Clear History",
                icon = Icons.Outlined.DeleteSweep,
                onClick = { onScreenSelected(Screen.ClearChat) },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun DrawerActionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

