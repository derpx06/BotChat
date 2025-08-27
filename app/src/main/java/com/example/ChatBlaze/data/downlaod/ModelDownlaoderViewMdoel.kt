package com.example.ChatBlaze.data.downlaod

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class ModelDownloaderViewModel(
    private val context: Context,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadModelsWithInitialStatus()
    }

    private fun getAvailableModels(): List<Model> {
        return listOf(
            Model(
                id = "gemma-2b-it-cpu-int8",
                name = "Gemma 2B IT (int8)",
                url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-gpu-int8.bin",
                description = "A lightweight, state-of-the-art open model from Google. Optimized for CPU with 8-bit quantization.",
                size = 2_500_000_000L,
                version = "main",
                category = "General Purpose",
                architecture = "Gemma"
            ),
            Model(
                id = "gemma-2b-it-cpu-int4",
                name = "Gemma 2B IT (int4)",
                url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-gpu-int4.bin",
                description = "A lightweight, state-of-the-art open model from Google. Optimized for CPU with 4-bit quantization.",
                size = 1_350_000_000L,
                version = "main",
                category = "General Purpose",
                architecture = "Gemma"
            ),
            Model(
                id = "phi3-instruct-q8",
                name = "Phi-3.5 Instruct (q8)",
                url = "https://huggingface.co/lokinfey/Phi-3.5-instruct-tflite/resolve/main/phi3_q8_seq1024_ekv1280.tflite",
                description = "Unofficial Phi-3.5-mini-instruct model converted to TFLite for on-device text generation.",
                size = 6_470_000_000L,
                version = "main",
                category = "General Purpose",
                architecture = "Phi-3"
            ),
            Model(
                    id = "gpt2-medium-64-fp16",
            name = "gpt2-medium (q16)",
            url = "https://huggingface.co/axtonyao/gpt2-fp16-tflite/resolve/main/gpt2-medium-64-fp16.tflite",
            description = "Unofficial Phi-3.5-mini-instruct model converted to TFLite for on-device text generation.",
            size = 676_000_000L,
            version = "main",
            category = "General Purpose",
            architecture = "Phi-3"
        ),
            Model(
                id = "llama-3.2-1b-q8",
                name = "LLaMA-3.2 1B (q8)",
                url = "https://huggingface.co/vimal-yuvabe/llama-3.2-1b-tflite/resolve/main/llama-3.2-1b-q8.tflite",
                description = "The LLaMA 3.2 1B model is a compact, high-performing language model, quantized to 8-bit for on-device efficiency.",
                size = 2_160_000_000L,
                version = "main",
                category = "Code Generation",
                architecture = "LLaMA-3.2"
            ),
            Model(
                id = "llama-3.2-1b-q16",
                name = "LLaMA-3.2 1B (fp16)",
                url = "https://huggingface.co/vimal-yuvabe/llama-3.2-1b-tflite/resolve/main/llama-3.2-1b-fp16.tflite",
                description = "The LLaMA 3.2 1B model is a compact, high-performing language model, quantized to 16-bit.",
                size = 4_280_000_000L,
                version = "main",
                category = "Code Generation",
                architecture = "LLaMA-3.2"
            ),
            Model(
                id = "mobilebert-text-classifier",
                name = "MobileBERT Classifier",
                url = "https://storage.googleapis.com/mediapipe-models/text_classifier/bert_classifier/float32/latest/bert_classifier.tflite",
                description = "A lightweight and fast BERT variant optimized for text classification on mobile devices.",
                size = 25_000_000L,
                version = "latest",
                category = "Specialized",
                architecture = "MobileBERT"
            ),
            Model(
                id = "blenderbot-small",
                name = "BlenderBot Small (90M)",
                url = "https://huggingface.co/jacob-valdez/blenderbot-small-tflite/resolve/main/blenderbot.tflite",
                description = "TFLite version of BlenderBot Small (90M) for open-domain conversational tasks.",
                size = 351_000_000L,
                version = "main",
                category = "Specialized",
                architecture = "BlenderBot"
            )
        )
    }

    private fun loadModelsWithInitialStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val availableModels = getAvailableModels()
            val modelsWithStatus = availableModels.map { model ->
                val file = getDestinationFile(context, model)
                if (file.exists() && file.length() >= model.size) {
                    model.copy(status = DownloadStatus.DOWNLOADED, progress = 100, downloadedMegabytes = model.size)
                } else {
                    file.delete()
                    model
                }
            }
            _uiState.update { it.copy(models = modelsWithStatus, isLoading = false) }
            modelsWithStatus.forEach { model ->
                observeWorkManagerByTag(model.id)
            }
        }
    }

    private fun observeWorkManagerByTag(modelId: String) {
        viewModelScope.launch {
            WorkManager.getInstance(context)
                .getWorkInfosByTagFlow(modelId)
                .map { workInfos ->
                    workInfos.find { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
                }
                .collectLatest { workInfo ->
                    val currentModel = _uiState.value.models.find { it.id == modelId }
                    if (workInfo != null && currentModel?.workId == null) {
                        updateModelStatusFromWorkInfo(modelId, workInfo)
                    }
                }
        }
    }

    fun startDownload(model: Model) {
        val file = getDestinationFile(context, model)
        if (file.exists() && file.length() >= model.size) {
            updateModelStatus(model.id, DownloadStatus.DOWNLOADED, 100, model.size)
            return
        }
        if (!hasEnoughSpace(model.size)) {
            _uiState.update { it.copy(errorToShow = "Not enough storage space.") }
            return
        }
        file.delete()
        val workId = downloadRepository.downloadModel(model)
        _uiState.update { state ->
            state.copy(models = state.models.map {
                if (it.id == model.id) it.copy(workId = workId) else it
            })
        }
        observeDownloadProgressById(model.id, workId)
    }

    private fun observeDownloadProgressById(modelId: String, workId: UUID) {
        viewModelScope.launch {
            WorkManager.getInstance(context)
                .getWorkInfoByIdFlow(workId)
                .collectLatest { workInfo ->
                    if (workInfo != null) {
                        updateModelStatusFromWorkInfo(modelId, workInfo)
                    }
                }
        }
    }

    fun deleteModel(model: Model) {
        viewModelScope.launch {
            downloadRepository.cancelDownload(model)
            val file = getDestinationFile(context, model)
            if (file.exists()) {
                file.delete()
            }
            _uiState.update { state ->
                state.copy(models = state.models.map {
                    if (it.id == model.id) {
                        it.copy(
                            status = DownloadStatus.NOT_DOWNLOADED,
                            progress = 0,
                            downloadedMegabytes = 0L,
                            workId = null
                        )
                    } else {
                        it
                    }
                })
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorToShow = null) }
    }

    private fun updateModelStatusFromWorkInfo(modelId: String, workInfo: WorkInfo) {
        val progress = workInfo.progress.getInt(KEY_PROGRESS, 0)
        val downloadedBytes = workInfo.progress.getLong(KEY_DOWNLOADED_BYTES, 0)

        val newStatus = when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> DownloadStatus.ENQUEUED
            WorkInfo.State.RUNNING -> DownloadStatus.DOWNLOADING
            WorkInfo.State.SUCCEEDED -> DownloadStatus.DOWNLOADED
            WorkInfo.State.FAILED -> {
                val error = workInfo.outputData.getString(KEY_ERROR_MSG) ?: "Unknown error"
                _uiState.update { it.copy(errorToShow = error) }
                DownloadStatus.FAILED
            }
            WorkInfo.State.CANCELLED -> DownloadStatus.NOT_DOWNLOADED
            else -> {
                _uiState.value.models.find { it.id == modelId }?.status ?: DownloadStatus.NOT_DOWNLOADED
            }
        }
        updateModelStatus(modelId, newStatus, progress, downloadedBytes, workInfo.id)
    }

    private fun updateModelStatus(modelId: String, status: DownloadStatus, progress: Int, downloadedBytes: Long, workId: UUID? = null) {
        _uiState.update { state ->
            state.copy(models = state.models.map {
                if (it.id == modelId) {
                    it.copy(
                        status = status,
                        progress = progress,
                        downloadedMegabytes = downloadedBytes,
                        workId = if (status == DownloadStatus.DOWNLOADING || status == DownloadStatus.ENQUEUED) workId ?: it.workId else null
                    )
                } else {
                    it
                }
            })
        }
    }

    private fun getDestinationFile(context: Context, model: Model): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        val extension = model.url.substringAfterLast('.', "bin")
        return File(dir, "${model.id}.$extension")
    }

    private fun hasEnoughSpace(requiredBytes: Long): Boolean {
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return false
        val stat = StatFs(storageDirectory.path)
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        return availableBytes > requiredBytes * 1.1
    }
}

class ModelDownloaderViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModelDownloaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository = DefaultDownloadRepository(context.applicationContext)
            return ModelDownloaderViewModel(context.applicationContext, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}