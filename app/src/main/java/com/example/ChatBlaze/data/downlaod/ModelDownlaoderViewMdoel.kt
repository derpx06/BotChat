package com.example.ChatBlaze.data.download

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.ChatBlaze.data.downlaod.DefaultDownloadRepository
import com.example.ChatBlaze.data.downlaod.DownloadRepository
import com.example.ChatBlaze.data.downlaod.KEY_ERROR_MSG
import com.example.ChatBlaze.data.downlaod.KEY_PROGRESS
import com.example.ChatBlaze.data.downlaod.Model
import com.example.ChatBlaze.data.downlaod.ModelDownloadStatus
import com.example.ChatBlaze.data.downlaod.ModelDownloadStatusType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ModelUiState(
    val models: List<Model> = emptyList(),
    val isLoading: Boolean = true,
    val errorToShow: String? = null
)

class ModelDownloaderViewModel(
    private val context: Context,
    val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadModelsWithInitialStatus()
    }

    private fun loadModelsWithInitialStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val availableModels = listOf(
                Model(
                    id = "phi3-instruct-q8",
                    name = "Phi-3.5 Instruct (q8)",
                    url = "https://huggingface.co/lokinfey/Phi-3.5-instruct-tflite/resolve/main/phi3_q8_seq1024_ekv1280.tflite",
                    description = "The Phi-3.5-instruct-tflite model, as described, is an unofficial version created for testing and development purposes, based on the Phi-3.5-mini-instruct model and converted using AI Edge Torch, a Python library from Google that supports converting PyTorch models into the .tflite format for on-device execution with TensorFlow Lite and MediaPipe, suitable for Android, iOS, and IoT applications. This model leverages the Android LLM Inference API, enabling on-device large language model tasks such as text generation, information retrieval in natural language, and document summarization, with support for multiple text-to-text models. AI Edge Torch provides broad CPU coverage and initial GPU and NPU support, integrating closely with PyTorch and covering Core ATen operators. The conversion process requires Python 3.10.12, preferably installed via conda, and is recommended for use on Ubuntu 20.04/22.04 or cloud VMs like Azure Linux. The setup involves cloning the AI Edge Torch repository, installing necessary Python libraries including TensorFlow CPU and MediaPipe, and downloading the Microsoft Phi-3.5-mini-instruct model from Hugging Face. Conversion to .tflite involves a specific Python script with parameters for sequence length and quantization, while further conversion to an Android MediaPipe bundle requires additional configuration using MediaPipe’s bundler, specifying paths for the .tflite model, tokenizer, and output. The model has seen 3 downloads in the last month and is not currently deployed by any Inference Provider, with an option for users to request support. No specific benchmarks or limitations are detailed in the provided content.",
                    size = "6.5GB",
                    version = "main"
                ),
                Model(
                    id = "blenderbot-small",
                    name = "BlenderBot Small (90M)",
                    url = "https://huggingface.co/jacob-valdez/blenderbot-small-tflite/resolve/main/blenderbot.tflite",
                    description = "The model card for `blenderbot-small-tflite` describes a TensorFlow Lite (tflite) version of the `blenderbot-small-90M` model, converted by jacob-valdez for use in the UTA CSE3310 class. The conversion process is detailed in a Google Drive document available at https://drive.google.com/file/d/1F93nMsDIm1TWhn70FcLtcaKQUynHq9wS/view?usp=sharing, and further information can be found in the associated GitHub repository at https://github.com/kmosoti/DesparadosAEYE. The model requires user and model input integers to be right-padded to a shape of [32,], with the true length indicated by the third and fourth parameters. Input and output details are provided, including specifications for `input_tokens`, `decoder_input_tokens`, `input_len`, and `decoder_input_len`, all of which use numpy.int32 dtype and have specific quantization and shape attributes. The output is identified as `Identity` with a shape of [1]. The model has had 15 downloads in the last month and is not currently deployed by any Inference Provider, with an option for users to request support for deployment. No specific capabilities, intended use, limitations, or benchmarks are detailed in the provided content.",
                    size = "351MB",
                    version = "main"
                ),
                Model(
                    id = "gemma-2b-it-cpu-int4",
                    name = "Gemma-2B-IT CPU int4",
                    url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-cpu-int4.bin",
                    description = "The google/gemma-2b-it-tflite model, as detailed in its Hugging Face repository, is a TensorFlow Lite implementation of the Gemma 2B instruction-tuned model, optimized for on-device machine learning with 4-bit and 8-bit quantization, and compatibility with both GPU and CPU. This 2B parameter base model is specifically tuned to respond to prompts in a conversational manner, making it suitable for interactive applications. Access to the model requires users to log in or sign up on Hugging Face and agree to Google's usage license, with requests processed immediately upon acceptance. The model page is hosted on Kaggle at https://www.kaggle.com/models/google/gemma/tfLite, and its terms of use are available at https://www.kaggle.com/models/google/gemma/license/consent/verify/huggingface?returnModelRepoId=google/gemma-2b-it-tflite. The model is authored by Google, but specific capabilities, intended uses, limitations, and benchmarks are not detailed in the provided content. Downloads for this model are not tracked, and it is not currently deployed by any inference providers, though users can request support for deployment.",
                    size = "1.35GB",
                    version = "main"
                ),
                Model(
                    id = "gemma-2b-it-cpu-int8",
                    name = "Gemma-2B-IT CPU int8",
                    url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-cpu-int8.bin",
                    description = "The google/gemma-2b-it-tflite model, as detailed in its Hugging Face repository, is a TensorFlow Lite implementation of the Gemma 2B instruction-tuned model, optimized for on-device machine learning with 4-bit and 8-bit quantization, and compatibility with both GPU and CPU. This 2B parameter base model is specifically tuned to respond to prompts in a conversational manner, making it suitable for interactive applications. Access to the model requires users to log in or sign up on Hugging Face and agree to Google's usage license, with requests processed immediately upon acceptance. The model page is hosted on Kaggle at https://www.kaggle.com/models/google/gemma/tfLite, and its terms of use are available at https://www.kaggle.com/models/google/gemma/license/consent/verify/huggingface?returnModelRepoId=google/gemma-2b-it-tflite. The model is authored by Google, but specific capabilities, intended uses, limitations, and benchmarks are not detailed in the provided content. Downloads for this model are not tracked, and it is not currently deployed by any inference providers, though users can request support for deployment.",
                    size = "2.5GB",
                    version = "main"
                ),
                Model(
                    id = "gemma-2b-it-gpu-int4",
                    name = "Gemma-2B-IT GPU int4",
                    url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-gpu-int4.bin",
                    description = "The google/gemma-2b-it-tflite model, as detailed in its Hugging Face repository, is a TensorFlow Lite implementation of the Gemma 2B instruction-tuned model, optimized for on-device machine learning with 4-bit and 8-bit quantization, and compatibility with both GPU and CPU. This 2B parameter base model is specifically tuned to respond to prompts in a conversational manner, making it suitable for interactive applications. Access to the model requires users to log in or sign up on Hugging Face and agree to Google's usage license, with requests processed immediately upon acceptance. The model page is hosted on Kaggle at https://www.kaggle.com/models/google/gemma/tfLite, and its terms of use are available at https://www.kaggle.com/models/google/gemma/license/consent/verify/huggingface?returnModelRepoId=google/gemma-2b-it-tflite. The model is authored by Google, but specific capabilities, intended uses, limitations, and benchmarks are not detailed in the provided content. Downloads for this model are not tracked, and it is not currently deployed by any inference providers, though users can request support for deployment.",
                    size = "1.35GB",
                    version = "main"
                ),
                Model(
                    id = "gemma-2b-it-gpu-int8",
                    name = "Gemma-2B-IT GPU int8",
                    url = "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-gpu-int8.bin",
                    description = "The google/gemma-2b-it-tflite model, as detailed in its Hugging Face repository, is a TensorFlow Lite implementation of the Gemma 2B instruction-tuned model, optimized for on-device machine learning with 4-bit and 8-bit quantization, and compatibility with both GPU and CPU. This 2B parameter base model is specifically tuned to respond to prompts in a conversational manner, making it suitable for interactive applications. Access to the model requires users to log in or sign up on Hugging Face and agree to Google's usage license, with requests processed immediately upon acceptance. The model page is hosted on Kaggle at https://www.kaggle.com/models/google/gemma/tfLite, and its terms of use are available at https://www.kaggle.com/models/google/gemma/license/consent/verify/huggingface?returnModelRepoId=google/gemma-2b-it-tflite. The model is authored by Google, but specific capabilities, intended uses, limitations, and benchmarks are not detailed in the provided content. Downloads for this model are not tracked, and it is not currently deployed by any inference providers, though users can request support for deployment.",
                    size = "2.5GB",
                    version = "main"
                ),
                Model(
                    id = "llama2-7b-q4",
                    name = "LLaMA-2-7B int4",
                    url = "https://huggingface.co/second-state/Llama-2-7b-tflite/resolve/main/llama2-7b-q4.tflite",
                    description = "Quantized LLaMA-2-7B int4",
                    size = "4.2GB",
                    version = "main"
                ),
                Model(
                    id = "llama2-7b-q8",
                    name = "LLaMA-2-7B int8",
                    url = "https://huggingface.co/second-state/Llama-2-7b-tflite/resolve/main/llama2-7b-q8.tflite",
                    description = "Quantized LLaMA-2-7B int8",
                    size = "7.8GB",
                    version = "main"
                ),
                Model(
                    id = "mistral-7b-q4",
                    name = "Mistral-7B int4",
                    url = "https://huggingface.co/second-state/Mistral-7b-tflite/resolve/main/mistral-7b-q4.tflite",
                    description = "Quantized Mistral-7B int4",
                    size = "4.3GB",
                    version = "main"
                ),
                Model(
                    id = "mistral-7b-q8",
                    name = "Mistral-7B int8",
                    url = "https://huggingface.co/second-state/Mistral-7b-tflite/resolve/main/mistral-7b-q8.tflite",
                    description = "Quantized Mistral-7B int8",
                    size = "7.9GB",
                    version = "main"
                ),
                Model(
                    id = "mobilebert-text-classifier",
                    name = "MobileBERT Classifier",
                    url = "https://storage.googleapis.com/mediapipe-models/text_classifier/bert_classifier/float32/latest/bert_classifier.tflite",
                    description = "MobileBERT is a lightweight and efficient variant of BERT, specifically designed for resource-limited devices such as mobile phones. The MediaPipe Text Classifier task lets you classify text into a set of defined categories, such as positive or negative sentiment. The MediaPipe Model Maker package is a simple, low-code solution for customizing on-device machine learning (ML) Models. Finetune and deploy on-device the MobileBERT Classifier models by using MediaPipe.",
                    size = "25MB",
                    version = "latest"
                ),
                Model(
                    id = "llama-3.2-1b-fp16",
                    name = "LLaMA-3.2-1B FP16",
                    url = "https://huggingface.co/vimal-yuvabe/llama-3.2-1b-tflite/resolve/main/llama-3.2-1b-fp16.tflite",
                    description = "LLaMA-3.2-1B FP16 model in TFLite format",
                    size = "4.28GB",
                    version = "main"
                ),
                Model(
                    id = "llama-3.2-1b-q8",
                    name = "LLaMA-3.2-1B q8",
                    url = "https://huggingface.co/vimal-yuvabe/llama-3.2-1b-tflite/resolve/main/llama-3.2-1b-q8.tflite",
                    description = "Quantized LLaMA-3.2-1B q8 model in TFLite format",
                    size = "2.16GB",
                    version = "main"
                )
            )

            val downloadedIds = getDownloadedModelIds()
            val modelsWithStatus = availableModels.map { model ->
                val file = getDestinationFile(context, model.id)
                if (file.exists() && file.length() > 0) {
                    model.copy(downloadStatus = ModelDownloadStatus(
                        ModelDownloadStatusType.SUCCEEDED,
                        100
                    )
                    )
                } else {
                    file.delete()
                    model
                }
            }
            _uiState.update { it.copy(models = modelsWithStatus, isLoading = false) }
        }
    }

    private fun getDownloadedModelIds(): Set<String> {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return emptySet()
        return directory.listFiles()?.map { it.nameWithoutExtension }?.toSet() ?: emptySet()
    }

    private fun getDestinationFile(context: Context, modelId: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        return File(dir, "$modelId.tflite")
    }

    fun startDownload(model: Model) {
        val workId = downloadRepository.downloadModel(model)
        viewModelScope.launch {
            downloadRepository.getDownloadProgress(workId).collectLatest { workInfo ->
                if (workInfo != null) {
                    updateModelStatusFromWorkInfo(model.id, workInfo)
                }
            }
        }
    }

    fun deleteModel(model: Model) {
        viewModelScope.launch {
            val file = getDestinationFile(context, model.id)
            if (file.exists()) file.delete()
            updateModelStatus(model.id, ModelDownloadStatus(ModelDownloadStatusType.NOT_DOWNLOADED))
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorToShow = null) }
    }

    private fun updateModelStatusFromWorkInfo(modelId: String, workInfo: WorkInfo) {
        val newStatus = when (workInfo.state) {
            WorkInfo.State.RUNNING -> ModelDownloadStatus(ModelDownloadStatusType.IN_PROGRESS, workInfo.progress.getInt(
                KEY_PROGRESS, 0))
            WorkInfo.State.SUCCEEDED -> ModelDownloadStatus(ModelDownloadStatusType.SUCCEEDED, 100)
            WorkInfo.State.FAILED -> {
                val error = workInfo.outputData.getString(KEY_ERROR_MSG) ?: "Unknown error"
                _uiState.update { it.copy(errorToShow = error) }
                ModelDownloadStatus(ModelDownloadStatusType.FAILED, errorMessage = error)
            }
            WorkInfo.State.CANCELLED -> ModelDownloadStatus(ModelDownloadStatusType.CANCELLED)
            else -> null
        }

        if (newStatus != null) {
            _uiState.update { state ->
                state.copy(models = state.models.map {
                    if (it.id == modelId) it.copy(downloadStatus = newStatus) else it
                })
            }
        }
    }

    private fun updateModelStatus(modelId: String, newStatus: ModelDownloadStatus) {
        _uiState.update { state ->
            state.copy(models = state.models.map {
                if (it.id == modelId) it.copy(downloadStatus = newStatus) else it
            })
        }
    }
}

class ModelDownloaderViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModelDownloaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository: DownloadRepository =
                DefaultDownloadRepository(context.applicationContext)
            return ModelDownloaderViewModel(context.applicationContext, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}