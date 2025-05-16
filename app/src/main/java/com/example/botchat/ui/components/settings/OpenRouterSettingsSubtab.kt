package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.database.modelDatabase.SelectedModel
import com.example.botchat.database.modelDatabase.modelDao
import com.example.botchat.ui.theme.MidnightBlack
import com.example.botchat.ui.theme.PureWhite
import com.example.botchat.ui.theme.SlateBlack
import com.example.botchat.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OpenRouterSettingsSubTab(
    settingViewModel: SettingViewModel,
    modelDao: modelDao,
    onNavigateToModels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedModels by remember { mutableStateOf<List<SelectedModel>>(emptyList()) }
    var selectedModel by remember { mutableStateOf<SelectedModel?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val currentModelId by settingViewModel.openRouterModel.collectAsStateWithLifecycle(initialValue = "")
    val apiKey by settingViewModel.openRouterApiKey.collectAsStateWithLifecycle(initialValue = "")
    var showApiKey by remember { mutableStateOf(false) }

    // Fetch selected models from the database and sync with current model ID
    LaunchedEffect(Unit) {
        modelDao.getAllModels().collectLatest { models ->
            selectedModels = models
            selectedModel = models.find { it.modelId == currentModelId } ?: models.firstOrNull()
            if (selectedModel != null && currentModelId != selectedModel?.modelId) {
                settingViewModel.updateOpenRouterModel(selectedModel!!.modelId)
            }
        }
    }

    // Animation for dropdown scale
    val dropdownScale by animateFloatAsState(
        targetValue = if (isDropdownExpanded) 1.02f else 1f,
        animationSpec = tween(200),
        label = "dropdownScale"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "OpenRouter Configuration",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // API Key Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ApiKeyInput(
                    apiKey = apiKey,
                    showApiKey = showApiKey,
                    onApiKeyChange = { settingViewModel.updateOpenRouterApiKey(it) },
                    onVisibilityToggle = { showApiKey = !showApiKey },
                    label = "OpenRouter API Key",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Selected Models Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Active Model",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (selectedModels.isEmpty()) {
                        Text(
                            text = "No models selected. Add a model to get started.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        )
                    } else {
                        // Model Dropdown
                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .scale(dropdownScale)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { isDropdownExpanded = true },
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = selectedModel?.name ?: "Select a model",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (selectedModel != null) {
                                            Text(
                                                text = selectedModel?.description ?: "",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand dropdown",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                selectedModels.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = model.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                                Text(
                                                    text = model.description,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        },
                                        onClick = {
                                            scope.launch {
                                                selectedModel = model
                                                settingViewModel.updateOpenRouterModel(model.modelId)
                                                isDropdownExpanded = false
                                                snackbarHostState.showSnackbar("Selected ${model.name}")
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Delete Selected Model Button
                        if (selectedModel != null) {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        modelDao.deleteSelectedModel(selectedModel!!)
                                        snackbarHostState.showSnackbar("Deleted ${selectedModel!!.name}")
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Model",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Remove Model",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            // Add New Model Button
            Button(
                onClick = onNavigateToModels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Model")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Model",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
                )
            }

            // Clear All Models Button
            if (selectedModels.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            modelDao.deleteAllModels()
                            snackbarHostState.showSnackbar("Cleared all models")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = "Clear All Models",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
                    )
                }
            }
        }
    }
}