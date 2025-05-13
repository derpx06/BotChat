package com.example.botchat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.data.Architecture
import com.example.botchat.data.Pricing
import com.example.botchat.data.TopProvider
import kotlinx.coroutines.delay

@Composable
fun ModelList(
    models: List<OpenRouterModel>,
    isDarkTheme: Boolean,
    theme: String,
    searchQuery: String,
    filters: Set<String>,
    sortBy: SortOption,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterChange: (String) -> Unit,
    onSortByChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            FilterColumn(
                filters = filters,
                onFilterChange = onFilterChange,
                sortBy = sortBy,
                onSortByChange = onSortByChange
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(models) { model ->
            ModelCard(
                model = model,
                isDarkTheme = isDarkTheme,
                theme = theme,
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(400, easing = LinearOutSlowInEasing),
                        placementSpec = tween(400, easing = LinearOutSlowInEasing)
                    )
            )
        }
    }
}

@Composable
fun ModelCard(
    model: OpenRouterModel,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isFreeModel = model.id.contains(":free", ignoreCase = true)
    val inputType = model.architecture?.inputModalities?.joinToString(", ") ?: "Text"

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isDarkTheme)
                MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(animationSpec = tween(300, easing = LinearOutSlowInEasing))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isFreeModel) {
                            Badge(
                                text = "Free",
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                        Badge(
                            text = inputType,
                            color = Color(0xFF2196F3)
                        )
                        model.architecture?.modality?.let {
                            Badge(
                                text = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                color = Color(0xFF9C27B0)
                            )
                        }
                        Badge(
                            text = formatParameters(estimateParameters(model)),
                            color = Color(0xFFF57C00)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedIconButton(
                        onClick = { /* Add Action */ },
                        icon = Icons.Default.Add,
                        contentDescription = "Add Model",
                        modifier = Modifier.size(32.dp)
                    )
                    AnimatedIconButton(
                        onClick = { expanded = !expanded },
                        icon = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200, easing = LinearOutSlowInEasing)) +
                        slideInVertically(tween(200, easing = LinearOutSlowInEasing), initialOffsetY = { it / 8 }),
                exit = fadeOut(tween(150, easing = FastOutLinearInEasing)) +
                        slideOutVertically(tween(150, easing = FastOutLinearInEasing), targetOffsetY = { it / 8 })
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        model.id.let {
                            ExpandedInfoItem(
                                label = "Model ID",
                                value = it,
                                icon = Icons.Default.Info
                            )
                        }
                        model.description?.let {
                            ExpandedInfoItem(
                                label = "Description",
                                value = it,
                                icon = Icons.Default.List
                            )
                        }
                        model.contextLength?.let {
                            ExpandedInfoItem(
                                label = "Context Length",
                                value = "$it tokens",
                                icon = Icons.Default.Email
                            )
                        }
                        model.architecture?.tokenizer?.let {
                            ExpandedInfoItem(
                                label = "Tokenizer",
                                value = it,
                                icon = Icons.Default.Menu
                            )
                        }
                        model.pricing?.prompt?.let {
                            ExpandedInfoItem(
                                label = "Prompt Pricing",
                                value = it,
                                icon = Icons.Default.AccountCircle
                            )
                        }
                        model.topProvider?.maxCompletionTokens?.let {
                            ExpandedInfoItem(
                                label = "Max Completion Tokens",
                                value = it.toString(),
                                icon = Icons.Default.Create
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var clicked by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (clicked) 0.95f else 1f,
        animationSpec = tween(150, easing = LinearOutSlowInEasing),
        label = "iconScale"
    )

    IconButton(
        onClick = {
            clicked = true
            onClick()
        },
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .scale(animatedScale)
                .size(24.dp)
        )
    }
    LaunchedEffect(clicked) {
        if (clicked) {
            delay(150)
            clicked = false
        }
    }
}

@Composable
fun ExpandedInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(200, easing = LinearOutSlowInEasing)) +
                scaleIn(tween(200, easing = LinearOutSlowInEasing)),
        exit = fadeOut(tween(150, easing = FastOutLinearInEasing))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = if (label == "Description") 3 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun Badge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModelListPreview() {
    MaterialTheme {
        val sampleModels = listOf(
            OpenRouterModel(
                id = "model:free",
                name = "Sophisticated AI",
                created = 1625097600,
                description = "A free, high-performance model for natural language processing tasks",
                contextLength = 32000,
                architecture = Architecture(
                    inputModalities = listOf("text"),
                    outputModalities = listOf("text"),
                    modality = "text",
                    tokenizer = "custom",
                    instructType = "chat"
                ),
                pricing = Pricing(
                    prompt = "$0.01",
                    completion = "$0.02",
                    request = null,
                    image = null,
                    webSearch = null,
                    internalReasoning = null
                ),
                topProvider = TopProvider(
                    contextLength = 32000,
                    maxCompletionTokens = 4096,
                    isModerated = true
                ),
                perRequestLimits = null,
                supportedParameters = listOf("temperature", "max_tokens")
            ),
            OpenRouterModel(
                id = "model:pro",
                name = "Elite AI Model",
                created = 1625184000,
                description = "Advanced multimodal model supporting both text and image inputs",
                contextLength = 128000,
                architecture = Architecture(
                    inputModalities = listOf("image", "text"),
                    outputModalities = listOf("text"),
                    modality = "multimodal",
                    tokenizer = "advanced",
                    instructType = "chat"
                ),
                pricing = Pricing(
                    prompt = "$0.05",
                    completion = "$0.10",
                    request = null,
                    image = null,
                    webSearch = null,
                    internalReasoning = null
                ),
                topProvider = TopProvider(
                    contextLength = 128000,
                    maxCompletionTokens = 8192,
                    isModerated = false
                ),
                perRequestLimits = null,
                supportedParameters = listOf("temperature", "max_tokens", "top_p")
            )
        )
        ModelList(
            models = sampleModels,
            isDarkTheme = false,
            theme = "plain",
            searchQuery = "",
            filters = setOf("Free", "Text"),
            sortBy = SortOption.NAME,
            onSearchQueryChange = {},
            onClearSearch = {},
            onFilterChange = {},
            onSortByChange = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        )
    }
}