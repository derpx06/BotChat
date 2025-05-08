package com.example.botchat.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.theme.BackgroundGradientDark
import com.example.botchat.ui.theme.MidnightBlack
import com.example.botchat.viewmodel.openrouterModels.ModelUIState
import com.example.botchat.viewmodel.openrouterModels.ModelViewModel
import com.example.botchat.viewmodel.setting.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(
    settingViewModel: SettingViewModel = viewModel(),
    modelViewModel: ModelViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            val dataStore = UserSettingsDataStore(LocalContext.current)
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ModelViewModel(dataStore) as T
            }
        }
    ),
    modifier: Modifier = Modifier
) {
    // collect flows
    val isDarkTheme = settingViewModel.getDarkModeEnabled()
    val theme by settingViewModel.theme.collectAsState(initial = "gradient")
    val uiState by modelViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // build a background-only modifier
    val bgModifier = when {
        theme == "gradient" || theme == "mixed" -> Modifier.background(
            brush = if (isDarkTheme) {
                BackgroundGradientDark
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE6E6FA), Color(0xFFF0F8FF))
                )
            }
        )

        theme == "plain" -> Modifier.background(
            color = if (isDarkTheme) MidnightBlack else Color.White
        )

        else -> Modifier.background(color = MaterialTheme.colorScheme.background)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Models", style = MaterialTheme.typography.titleMedium) }
            )
        },
        modifier = modifier
            .fillMaxSize()
            .then(bgModifier)    // apply background here
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.labelMedium
            )

            when (uiState) {
                is ModelUIState.Loading -> {
                    ShimmerList(innerPadding = innerPadding)
                }
                is ModelUIState.Success -> {
                    val filteredModels = (uiState as ModelUIState.Success).models.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.id.contains(searchQuery, ignoreCase = true)
                    }
                    ModelList(
                        models = filteredModels,
                        isDarkTheme = isDarkTheme,
                        theme = theme,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                is ModelUIState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (uiState as ModelUIState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Button(
                            onClick = { modelViewModel.retry() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelList(
    models: List<OpenRouterModel>,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(models) { model ->
            ModelCard(model, isDarkTheme, theme)
        }
    }
}

@Composable
fun ModelCard(model: OpenRouterModel, isDarkTheme: Boolean, theme: String) {
    var expanded by remember { mutableStateOf(false) }
    val isFreeModel = model.id.contains(":free", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { expanded = !expanded }
            .border(
                width = 0.5.dp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (theme == "plain") {
                if (isDarkTheme) MidnightBlack else Color.White
            } else {
                if (isDarkTheme) Color(0xFF1A0033) else Color(0xFFF0F8FF)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isFreeModel) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .background(
                                color = Color(0xFF4CAF50),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Free",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = if (expanded) "Collapse" else "Expand",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${model.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                model.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                model.contextLength?.let {
                    Text(
                        text = "Context: $it tokens",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
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
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.5f),
                                Color.Gray.copy(alpha = 0.2f),
                                Color.Gray.copy(alpha = 0.5f)
                            ),
                            start = androidx.compose.ui.geometry.Offset(shimmerTranslate, 0f),
                            end = androidx.compose.ui.geometry.Offset(shimmerTranslate + 1000f, 0f)
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewModelScreen() {
    ModelScreen()
}