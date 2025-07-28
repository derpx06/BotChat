package com.example.ChatBlaze.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ChatBlaze.data.database.modelDatabase.modelDao
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.ui.theme.CloudWhite
import com.example.ChatBlaze.ui.theme.MidnightBlack
import com.example.ChatBlaze.ui.viewmodel.openrouterModels.ModelUIState
import com.example.ChatBlaze.ui.viewmodel.openrouterModels.ModelViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    NAME("Name"),
    DATE_UPDATED("Date Updated"),
    CONTEXT_LENGTH("Context Length"),
    BILLION_PARAMETERS("Parameters")
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = tween(200, easing = LinearOutSlowInEasing),
        label = "searchBarScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .scale(scale),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            placeholder = {
                Text(
                    text = "Search models...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    var isClearPressed by remember { mutableStateOf(false) }
                    val clearScale by animateFloatAsState(
                        targetValue = if (isClearPressed) 0.95f else 1f,
                        animationSpec = tween(150, easing = LinearOutSlowInEasing),
                        label = "clearIconScale"
                    )
                    IconButton(
                        onClick = {
                            isClearPressed = true
                            onClear()
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .scale(clearScale)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    LaunchedEffect(isClearPressed) {
                        if (isClearPressed) {
                            delay(150)
                            isClearPressed = false
                        }
                    }
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = { /* Handled by onValueChange */ }
            )
        )
    }
    LaunchedEffect(searchQuery) {
        isFocused = searchQuery.isNotEmpty()
    }
}

@Composable
fun FilterColumn(
    filters: Set<String>,
    onFilterChange: (String) -> Unit,
    sortBy: SortOption,
    onSortByChange: (SortOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            SortByDropdown(
                sortBy = sortBy,
                onSortByChange = onSortByChange
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Free", "Image", "Text").forEach { filter ->
                AnimatedFilterChip(
                    selected = filters.contains(filter),
                    onClick = { onFilterChange(filter) },
                    label = filter
                )
            }
        }
    }
}

@Composable
fun AnimatedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1f,
        animationSpec = tween(200, easing = LinearOutSlowInEasing),
        label = "chipScale"
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(200, easing = LinearOutSlowInEasing),
        label = "chipColor"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                fontWeight = FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            selectedContainerColor = containerColor,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.scale(scale)
    )
}

@Composable
fun SortByDropdown(
    sortBy: SortOption,
    onSortByChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.03f else 1f,
        animationSpec = tween(200, easing = LinearOutSlowInEasing),
        label = "dropdownScale"
    )

    Box {
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = {
                Text(
                    "Sort: ${sortBy.label}",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Sort Options",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.scale(scale)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clip(RoundedCornerShape(12.dp))
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onSortByChange(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShimmerList(innerPadding: PaddingValues) {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceContainer,
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                MaterialTheme.colorScheme.surfaceContainer
                            ),
                            start = Offset(shimmerTranslate, 0f),
                            end = Offset(shimmerTranslate + 1000f, 0f)
                        )
                    )
                    .animateItemPlacement(
                        animationSpec = tween(400, easing = LinearOutSlowInEasing)
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
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Retry",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(
    settingViewModel: SettingViewModel,
    modelViewModel: ModelViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            val dataStore = UserSettingsDataStore(LocalContext.current)
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ModelViewModel(dataStore) as T
            }
        }
    ),
    modelDao: modelDao,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val theme by settingViewModel.theme.collectAsState(initial = "plain")
    val uiState by modelViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(SortOption.NAME) }
    var filters by remember { mutableStateOf(setOf<String>()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val bgColor = if (isDarkTheme) MidnightBlack else CloudWhite

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Discover AI Models",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClear = { searchQuery = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            when (val currentState = uiState) {
                is ModelUIState.Loading -> ShimmerList(innerPadding = innerPadding)
                is ModelUIState.Success -> {
                    val filteredModels = currentState.models
                        .filter { model ->
                            val matchesSearch = model.name.contains(searchQuery, ignoreCase = true) ||
                                    model.id.contains(searchQuery, ignoreCase = true)
                            val matchesFilters = filters.isEmpty() || filters.any { filter ->
                                when (filter) {
                                    "Free" -> model.id.contains(":free", ignoreCase = true)
                                    "Image" -> model.architecture?.inputModalities?.contains("image") == true
                                    "Text" -> model.architecture?.inputModalities?.contains("text") == true
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
                                SortOption.BILLION_PARAMETERS -> compareByDescending { model ->
                                    model.contextLength?.toLong() ?: 0 // Simplified for sorting
                                }
                            }
                        )
                    ModelList(
                        models = filteredModels,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        searchQuery = searchQuery,
                        filters = filters,
                        sortBy = sortBy,
                        modelDao = modelDao,
                        onSearchQueryChange = { searchQuery = it },
                        onClearSearch = { searchQuery = "" },
                        onFilterChange = { filter ->
                            filters = if (filters.contains(filter)) filters - filter else filters + filter
                        },
                        onSortByChange = { sortBy = it },
                        onModelSelected = { modelId ->
                            scope.launch {
                                onModelSelected(modelId)
                                snackbarHostState.showSnackbar("Model added")
                            }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is ModelUIState.Error -> ErrorScreen(
                    message = currentState.message,
                    onRetry = { modelViewModel.retry() }
                )
            }
        }
    }
}