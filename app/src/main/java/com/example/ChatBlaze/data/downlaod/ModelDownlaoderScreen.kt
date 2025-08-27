package com.example.ChatBlaze.data.downlaod

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDownloaderScreen(
    viewModel: ModelDownloaderViewModel = viewModel(factory = ModelDownloaderViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val groupedModels = uiState.models.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download Center") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedModels.forEach { (category, models) ->
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                            )
                        }
                        items(models, key = { it.id }) { model ->
                            ModelCard(
                                model = model,
                                onDownload = { viewModel.startDownload(model) },
                                onCancel = { viewModel.deleteModel(model) },
                                onDelete = { viewModel.deleteModel(model) }
                            )
                        }
                    }
                }
            }

            uiState.errorToShow?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
                    }
                )
            }
        }
    }
}

@Composable
fun ModelCard(
    model: Model,
    onDownload: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(text = formatSize(model.size))
                InfoChip(text = model.architecture)
            }

            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Box(modifier = Modifier.padding(top = 8.dp)) {
                when (model.status) {
                    DownloadStatus.DOWNLOADING, DownloadStatus.ENQUEUED -> DownloadProgressIndicator(
                        model,
                        onCancel
                    )
                    else -> ActionButtons(model, onDownload, onDelete)
                }
            }
        }
    }
}


@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DownloadProgressIndicator(model: Model, onCancel: () -> Unit) {
    val progressFraction = remember(model.progress) { model.progress / 100f }
    // FIXED: Changed model.downloadedMegabytes back to model.downloadedBytes
    val downloadedSize = remember(model.downloadedMegabytes) { formatSize(model.downloadedMegabytes) }
    val totalSize = remember(model.size) { formatSize(model.size) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (model.status == DownloadStatus.ENQUEUED) "Queued..." else "Downloading...",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$downloadedSize / $totalSize",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(CircleShape),
                strokeCap = StrokeCap.Round
            )
            OutlinedButton(
                onClick = onCancel,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel Download",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ActionButtons(model: Model, onDownload: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (model.status) {
            DownloadStatus.DOWNLOADED -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Downloaded",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Downloaded",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
            DownloadStatus.FAILED -> {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, "Retry", Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Retry")
                }
            }
            else -> {
                Button(onClick = onDownload) {
                    Icon(Icons.Default.Download, "Download", Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Download Model")
                }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes < 0) return "0 B"
    val df = DecimalFormat("#.##")
    return when {
        bytes >= 1_000_000_000 -> "${df.format(bytes / 1_000_000_000.0)} GB"
        bytes >= 1_000_000 -> "${df.format(bytes / 1_000_000.0)} MB"
        bytes >= 1_000 -> "${df.format(bytes / 1_000.0)} KB"
        else -> "$bytes B"
    }
}

class ModelPreviewParameterProvider : PreviewParameterProvider<Model> {
    override val values = sequenceOf(
        Model(
            id = "gemma-2b-it-cpu-int8",
            name = "Gemma 2B IT (int8)",
            url = "", description = "A lightweight, state-of-the-art open model from Google.",
            size = 2_500_000_000L, version = "main", category = "General", architecture = "Gemma",
            status = DownloadStatus.NOT_DOWNLOADED
        ),
        Model(
            id = "phi-3-mini",
            name = "Phi-3 Mini Instruct",
            url = "", description = "A powerful small language model by Microsoft Research.",
            size = 3_800_000_000L, version = "main", category = "General", architecture = "Phi-3",
            status = DownloadStatus.DOWNLOADING,
            progress = 45,
            downloadedMegabytes = 1_710_000_000L
        ),
        Model(
            id = "llama-3-8b",
            name = "LLaMA 3 8B Instruct",
            url = "", description = "The latest large language model from Meta.",
            size = 8_000_000_000L, version = "main", category = "General", architecture = "LLaMA 3",
            status = DownloadStatus.DOWNLOADED
        ),
        Model(
            id = "mobilebert",
            name = "MobileBERT Classifier",
            url = "", description = "A lightweight BERT variant for mobile text classification.",
            size = 25_000_000L, version = "latest", category = "Specialized", architecture = "MobileBERT",
            status = DownloadStatus.FAILED
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ModelCardStatePreview(
    @PreviewParameter(ModelPreviewParameterProvider::class) model: Model
) {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ModelCard(model = model, onDownload = {}, onCancel = {}, onDelete = {})
        }
    }
}