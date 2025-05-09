package com.example.botchat.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email

import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.ui.theme.MidnightBlack
import io.ktor.util.reflect.instanceOf

@Composable
fun ModelList(
    models: List<OpenRouterModel>,
    isDarkTheme: Boolean,
    theme: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(models) { model ->
            ModelCard(model = model, isDarkTheme = isDarkTheme, theme = theme)
        }
    }
}

@Composable
fun ModelCard(model: OpenRouterModel, isDarkTheme: Boolean, theme: String) {
    var expanded by remember { mutableStateOf(false) }
    val isFreeModel = model.id.contains(":free", ignoreCase = true)
    val inputType = model.architecture?.inputModalities?.joinToString(", ") ?: "Text"
    val parameters = formatParameters(estimateParameters(model))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .border(
                width = 1.5.dp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (theme == "plain") {
                if (isDarkTheme) MidnightBlack else Color.White
            } else {
                if (isDarkTheme) Color(0xFF1A0033).copy(alpha = 0.95f) else Color(0xFFF0F8FF).copy(alpha = 0.95f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isFreeModel) {
                            Badge(text = "Free", color = Color(0xFF4CAF50))
                        }
                        Badge(text = inputType, color = MaterialTheme.colorScheme.secondary)
                        model.architecture?.modality?.let {
                            Badge(text = it, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Badge(text = parameters, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
                Row {
                    IconButton(onClick = { /* Dummy Add Action */ }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Model",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExpandedInfoItem(
                        label = "Model ID",
                        value = model.id,
                        icon = Icons.Default.Info
                    )
                    model.description?.let {
                        ExpandedInfoItem(
                            label = "Description",
                            value = it,
                            icon = Icons.Default.Info
                        )
                    }
                    model.contextLength?.let {
                        ExpandedInfoItem(
                            label = "Context Length",
                            value = "$it tokens",
                            icon = Icons.Default.Menu
                        )
                    }
                    model.architecture?.tokenizer?.let {
                        ExpandedInfoItem(
                            label = "Tokenizer",
                            value = it,
                            icon = Icons.Default.AccountCircle
                        )
                    }
                    model.pricing?.prompt?.let {
                        ExpandedInfoItem(
                            label = "Prompt Pricing",
                            value = it,
                            icon = Icons.Default.Email
                        )
                    }
                    model.topProvider?.maxCompletionTokens?.let {
                        ExpandedInfoItem(
                            label = "Max Completion Tokens",
                            value = it.toString(),
                            icon = Icons.Default.Lock
                        )
                    }
                    Text(
                        text = "Estimated Parameters: $parameters",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandedInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

// Helper function to estimate parameters based on context length or model name
fun estimateParameters(model: OpenRouterModel): Long {
    // Heuristic: Larger context length or specific model names suggest more parameters
    val contextLength = model.contextLength ?: 8000
    val name = model.name.lowercase()
    return when {
        name.contains("405b") || contextLength > 128000 -> 405_000_000_000
        name.contains("70b") || contextLength > 32000 -> 70_000_000_000
        name.contains("8x22b") || contextLength > 16000 -> 22_000_000_000
        name.contains("8b") || contextLength > 8000 -> 8_000_000_000
        else -> 1_000_000_000 // Default to 1B for smaller models
    }
}

// Helper function to format parameters as a string (e.g., "8B")
fun formatParameters(parameters: Long): String {
    return when {
        parameters >= 1_000_000_000 -> "${parameters / 1_000_000_000}B"
        parameters >= 1_000_000 -> "${parameters / 1_000_000}M"
        else -> "$parameters"
    }
}