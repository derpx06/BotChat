package com.example.botchat.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.ui.theme.BackgroundGradientDark
import com.example.botchat.ui.theme.MidnightBlack
import com.example.botchat.viewmodel.openrouterModels.ModelUIState
import com.example.botchat.viewmodel.openrouterModels.ModelViewModel
import com.example.botchat.viewmodel.setting.SettingViewModel
import com.example.botchat.data.UserSettingsDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(
    settingViewModel: SettingViewModel = viewModel(),
    modelViewModel: ModelViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            val dataStore = UserSettingsDataStore(LocalContext.current)
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ModelViewModel(dataStore) as T
            }
        }
    ),
    modifier: Modifier = Modifier
) {
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val theme by settingViewModel.theme.collectAsState(initial = "gradient")
    val uiState by modelViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(SortOption.NAME) }
    var filters by remember { mutableStateOf(setOf<String>()) }

    val bgModifier = when {
        theme == "gradient" || theme == "mixed" -> Modifier.background(
            brush = if (isDarkTheme) BackgroundGradientDark else Brush.linearGradient(
                colors = listOf(Color(0xFFE6E6FA), Color(0xFFF0F8FF))
            )
        )
        theme == "plain" -> Modifier.background(
            color = if (isDarkTheme) MidnightBlack else Color.White
        )
        else -> Modifier.background(color = MaterialTheme.colorScheme.background)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Explore Models",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        modifier = modifier.fillMaxSize().then(bgModifier)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClear = { searchQuery = "" }
            )
            FilterColumn(
                filters = filters,
                onFilterChange = { filter ->
                    filters = if (filters.contains(filter)) filters - filter else filters + filter
                },
                sortBy = sortBy,
                onSortByChange = { sortBy = it }
            )
            when (uiState) {
                is ModelUIState.Loading -> ShimmerList(innerPadding = innerPadding)
                is ModelUIState.Success -> {
                    val filteredModels = (uiState as ModelUIState.Success).models
                        .filter { model ->
                            val matchesSearch = model.name.contains(searchQuery, ignoreCase = true) ||
                                    model.id.contains(searchQuery, ignoreCase = true)
                            val matchesFilters = filters.isEmpty() || filters.any { filter ->
                                when (filter) {
                                    "Free" -> model.id.contains(":free", ignoreCase = true)
                                    "Image" -> model.architecture?.inputModalities?.contains("image") == true
                                    "Text" -> model.architecture?.inputModalities?.contains("text") == true
                                    "Audio" -> model.architecture?.inputModalities?.contains("audio") == true
                                    "Above 8B" -> estimateParameters(model) > 8_000_000_000
                                    "Above 16B" -> estimateParameters(model) > 16_000_000_000
                                    "Above 80B" -> estimateParameters(model) > 80_000_000_000
                                    else -> false
                                }
                            }
                            matchesSearch && matchesFilters
                        }
                        .sortedWith(
                            when (sortBy) {
                                SortOption.NAME -> compareBy { it.name }
                                SortOption.DATE_UPDATED -> compareByDescending { it.created ?: 0 }
                                SortOption.CONTEXT_LENGTH -> compareByDescending { it.contextLength ?: 0 }
                                SortOption.BILLION_PARAMETERS -> compareByDescending { estimateParameters(it) }
                            }
                        )
                    ModelList(
                        models = filteredModels,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                is ModelUIState.Error -> ErrorScreen(
                    message = (uiState as ModelUIState.Error).message,
                    onRetry = { modelViewModel.retry() }
                )
            }
        }
    }
}

@Composable
fun ShimmerList(innerPadding: PaddingValues) {
    val transition = rememberInfiniteTransition()
    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.1f),
                                Color.Gray.copy(alpha = 0.3f)
                            ),
                            start = androidx.compose.ui.geometry.Offset(shimmerTranslate, 0f),
                            end = androidx.compose.ui.geometry.Offset(shimmerTranslate + 1000f, 0f)
                        )
                    )
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Retry",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}