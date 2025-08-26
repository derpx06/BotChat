package com.example.botchat.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ChatBlaze.data.downlaod.DefaultDownloadRepository
import com.example.ChatBlaze.data.downlaod.Model
import com.example.ChatBlaze.data.downlaod.ModelDownloadStatus
import com.example.ChatBlaze.data.downlaod.ModelDownloadStatusType
import com.example.ChatBlaze.data.download.ModelDownloaderViewModel
import com.example.ChatBlaze.data.download.ModelDownloaderViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDownloaderScreen(
    viewModel: ModelDownloaderViewModel = viewModel(factory = ModelDownloaderViewModelFactory(
        LocalContext.current
    )
    ),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Download Models") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("On-Device Models", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        Text("Download models for offline inference.", fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.models) { model ->
                                ModelCard(model = model, viewModel = viewModel)
                            }
                        }
                    }
                }

                if (uiState.errorToShow != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearError() },
                        title = { Text("Download Failed") },
                        text = { Text("An error occurred while downloading the model. Please check your connection and try again.") },
                        confirmButton = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModelCard(model: Model, viewModel: ModelDownloaderViewModel) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Column {
                Text(model.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = model.description,
                    fontSize = 14.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                    Text(if (expanded) "Show less" else "Show more", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(model.size, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (model.downloadStatus.status == ModelDownloadStatusType.IN_PROGRESS) {
                LinearProgressIndicator(
                    progress = { model.downloadStatus.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            ActionButtons(model = model, viewModel = viewModel)
        }
    }
}

@Composable
fun ActionButtons(model: Model, viewModel: ModelDownloaderViewModel) {
    val onDownloadOrRetry = { viewModel.startDownload(model) }
    val onCancel = { viewModel.downloadRepository.cancelDownload(model) }
    val onDelete = { viewModel.deleteModel(model) }

    Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
        when (model.downloadStatus.status) {
            ModelDownloadStatusType.NOT_DOWNLOADED -> {
                Button(onClick = onDownloadOrRetry) {
                    Icon(Icons.Default.Download, "Download", Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Download")
                }
            }
            ModelDownloadStatusType.IN_PROGRESS -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {}, enabled = false) {
                        Text("Downloading ${model.downloadStatus.progress}%", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Error, "Cancel", tint = Color.Red)
                    }
                }
            }
            ModelDownloadStatusType.SUCCEEDED -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, "Downloaded", tint = Color(0xFF66BB6A))
                    Spacer(Modifier.width(8.dp))
                    Text("Downloaded", color = Color(0xFF66BB6A))
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                    }
                }
            }
            ModelDownloadStatusType.FAILED, ModelDownloadStatusType.CANCELLED -> {
                Button(
                    onClick = onDownloadOrRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, "Retry", Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ModelDownloaderScreenPreview() {
    val context = LocalContext.current
    val viewModel: ModelDownloaderViewModel = viewModel(
        factory = ModelDownloaderViewModelFactory(context)
    )
    ModelDownloaderScreen(viewModel = viewModel, onNavigateBack = {})
}

@Preview
@Composable
fun ModelCardPreview() {
    val context = LocalContext.current
    val viewModel: ModelDownloaderViewModel = viewModel(
        factory = ModelDownloaderViewModelFactory(context)
    )
    val model = Model(
        id = "phi3-instruct-q8",
        name = "Phi-3.5 Instruct (q8)",
        url = "https://huggingface.co/lokinfey/Phi-3.5-instruct-tflite/resolve/main/phi3_q8_seq1024_ekv1280.tflite",
        description = "The Phi-3.5-instruct-tflite model, as described, is an unofficial version created for testing and development purposes, based on the Phi-3.5-mini-instruct model and converted using AI Edge Torch, a Python library from Google that supports converting PyTorch models into the .tflite format for on-device execution with TensorFlow Lite and MediaPipe, suitable for Android, iOS, and IoT applications. This model leverages the Android LLM Inference API, enabling on-device large language model tasks such as text generation, information retrieval in natural language, and document summarization, with support for multiple text-to-text models. AI Edge Torch provides broad CPU coverage and initial GPU and NPU support, integrating closely with PyTorch and covering Core ATen operators. The conversion process requires Python 3.10.12, preferably installed via conda, and is recommended for use on Ubuntu 20.04/22.04 or cloud VMs like Azure Linux. The setup involves cloning the AI Edge Torch repository, installing necessary Python libraries including TensorFlow CPU and MediaPipe, and downloading the Microsoft Phi-3.5-mini-instruct model from Hugging Face. Conversion to .tflite involves a specific Python script with parameters for sequence length and quantization, while further conversion to an Android MediaPipe bundle requires additional configuration using MediaPipe’s bundler, specifying paths for the .tflite model, tokenizer, and output. The model has seen 3 downloads in the last month and is not currently deployed by any Inference Provider, with an option for users to request support. No specific benchmarks or limitations are detailed in the provided content.",
        size = "6.5GB",
        version = "main",
        downloadStatus = ModelDownloadStatus(ModelDownloadStatusType.NOT_DOWNLOADED)
    )
    MaterialTheme(colorScheme = darkColorScheme()) {
        ModelCard(model = model, viewModel = viewModel)
    }
}

@Preview
@Composable
fun ActionButtonsNotDownloadedPreview() {
    val context = LocalContext.current
    val viewModel: ModelDownloaderViewModel = viewModel(
        factory = ModelDownloaderViewModelFactory(context)
    )
    val model = Model("1", "Test Model", "Test Desc", "url", "100MB", "1.0", ModelDownloadStatus(ModelDownloadStatusType.NOT_DOWNLOADED))
    MaterialTheme(colorScheme = darkColorScheme()) {
        ActionButtons(model = model, viewModel = viewModel)
    }
}

@Preview
@Composable
fun ActionButtonsInProgressPreview() {
    val context = LocalContext.current
    val viewModel: ModelDownloaderViewModel = viewModel(
        factory = ModelDownloaderViewModelFactory(context)
    )
    val model = Model("1", "Test Model", "Test Desc", "url", "100MB", "1.0", ModelDownloadStatus(ModelDownloadStatusType.IN_PROGRESS, 50))
    MaterialTheme(colorScheme = darkColorScheme()) {
        ActionButtons(model = model, viewModel = viewModel)
    }
}

@Preview
@Composable
fun ActionButtonsSucceededPreview() {
    val context = LocalContext.current
    val viewModel: ModelDownloaderViewModel = viewModel(
        factory = ModelDownloaderViewModelFactory(context)
    )
    val model = Model("1", "Test Model", "Test Desc", "url", "100MB", "1.0", ModelDownloadStatus(ModelDownloadStatusType.SUCCEEDED, 100))
    MaterialTheme(colorScheme = darkColorScheme()) {
        ActionButtons(model = model, viewModel = viewModel)
    }
}