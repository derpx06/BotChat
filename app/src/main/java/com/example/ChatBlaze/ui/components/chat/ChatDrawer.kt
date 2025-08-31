package com.example.ChatBlaze.ui.components.chat

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ChatBlaze.data.database.modelDatabase.modelDao
import com.example.ChatBlaze.ui.viewmodel.Chat.ChatViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private enum class DrawerState { Open, Closed }
private enum class Screen { Chat, Settings, Models, ClearChat, ModelDownloader }

private val DrawerWidth = 250.dp

@SuppressLint("RestrictedApi")
@Composable
fun ChatDrawer(
    chatViewModel: ChatViewModel,
    settingViewModel: SettingViewModel,
    onNavigateToModels: () -> Unit,
    onNavigateToDownloader: () -> Unit,
    modelDao: modelDao,
    isModelLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        val drawerState = remember { mutableStateOf(DrawerState.Closed) }
        val translationX = remember { Animatable(0f) }
        val drawerWidthPx = with(LocalDensity.current) { DrawerWidth.toPx() }
        val coroutineScope = rememberCoroutineScope()

        translationX.updateBounds(0f, drawerWidthPx)

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

        ChatDrawerContents(
            chatViewModel = chatViewModel,
            onScreenSelected = { screen ->
                when (screen) {
                    Screen.Settings -> settingViewModel.toggleSettings()
                    Screen.Models -> onNavigateToModels()
                    Screen.ModelDownloader -> onNavigateToDownloader()
                    Screen.ClearChat -> chatViewModel.clearMessages()
                    Screen.Chat -> {}
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
                    val cornerRadius = lerp(0f, 48.dp.toPx(), translationX.value / drawerWidthPx)
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
            ChatScreenContent(
                chatViewModel = chatViewModel,
                settingViewModel = settingViewModel,
                onNavigateToModels = onNavigateToModels,
                onDrawerClicked = ::toggleDrawer,
                modelDao = modelDao,
                isModelLoading = isModelLoading,
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
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp)
        ) {
            Text(
                text = "Conversations",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            val sessions by chatViewModel.allSessions.collectAsStateWithLifecycle(initialValue = emptyList())
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = sessions,
                    key = { it.sessionId }
                ) { session ->
                    val isSelected = session.sessionId == uiState.currentSessionId
                    Card(
                        modifier = Modifier.width(DrawerWidth),
                        shape = RoundedCornerShape(20.dp),
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
                                .padding(start = 20.dp, end = 8.dp),
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

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .width(DrawerWidth)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DrawerIconButton(
                    label = "New Chat",
                    icon = Icons.Outlined.AddComment,
                    onClick = {
                        chatViewModel.startNewChat()
                        onScreenSelected(Screen.Chat)
                    }
                )
                DrawerIconButton(
                    label = "Download",
                    icon = Icons.Outlined.Download,
                    onClick = { onScreenSelected(Screen.ModelDownloader) }
                )
                DrawerIconButton(
                    label = "Settings",
                    icon = Icons.Outlined.Settings,
                    onClick = { onScreenSelected(Screen.Settings) }
                )
                DrawerIconButton(
                    label = "Clear",
                    icon = Icons.Outlined.DeleteSweep,
                    onClick = { onScreenSelected(Screen.ClearChat) },
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun DrawerIconButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }

    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

@Preview(showBackground = true)
@Composable
fun DrawerIconButtonPreview() {
    DrawerIconButton(label = "New Chat", icon = Icons.Outlined.AddComment, onClick = {})
}