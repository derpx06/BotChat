package com.example.botchat.data.api

import android.content.Context
import com.example.ChatBlaze.data.download.ModelDownloaderViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class LlmInferenceHelper(
    private val context: Context,
    private val modelDownloaderViewModel: ModelDownloaderViewModel
) : AutoCloseable {
    private var interpreter: Interpreter? = null
    private var currentModelId: String? = null

    fun generateResponse(inputText: String, modelId: String): String {
        try {
            loadModel(modelId)
            val inputBuffer = prepareInput(inputText)
            val outputBuffer = prepareOutputBuffer()
            interpreter?.run(inputBuffer, outputBuffer)
            return decodeOutput(outputBuffer)
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate response with model $modelId: ${e.message}")
        }
    }

    fun generateResponseStream(inputText: String, modelId: String): Flow<String> = flow {
        try {
            loadModel(modelId)
            val inputBuffer = prepareInput(inputText)
            val outputBuffer = prepareOutputBuffer()
            interpreter?.run(inputBuffer, outputBuffer)
            val fullResponse = decodeOutput(outputBuffer)
            fullResponse.split(" ").forEach { word ->
                emit(word + " ")
                kotlinx.coroutines.delay(50)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to stream response with model $modelId: ${e.message}")
        }
    }

    private fun loadModel(modelId: String) {
        if (currentModelId == modelId && interpreter != null) return
        interpreter?.close()
        val modelFile = getModelFile(modelId)
        if (!modelFile.exists()) {
            throw IllegalStateException("Model file for $modelId not found")
        }
        interpreter = Interpreter(modelFile)
        currentModelId = modelId
    }

    private fun getModelFile(modelId: String): File {
        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)!!
        return File(dir, "$modelId.tflite").also {
            if (!it.exists()) {
                throw IllegalStateException("Model file $modelId.tflite not found")
            }
        }
    }

    private fun prepareInput(inputText: String): ByteBuffer {
        val model = modelDownloaderViewModel.uiState.value.models.find { it.id == currentModelId }
            ?: throw IllegalStateException("Model $currentModelId not found in ModelDownloaderViewModel")

        val maxInputLength = when (model.id) {
            "phi3-instruct-q8" -> 1024
            "blenderbot-small" -> 32
            "gemma-2b-it-cpu-int4", "gemma-2b-it-cpu-int8", "gemma-2b-it-gpu-int4", "gemma-2b-it-gpu-int8" -> 512
            "llama2-7b-q4", "llama2-7b-q8" -> 2048
            "mistral-7b-q4", "mistral-7b-q8" -> 2048
            "mobilebert-text-classifier" -> 512
            "llama-3.2-1b-fp16", "llama-3.2-1b-q8" -> 2048
            else -> 512
        }

        val tokens = tokenizeInput(inputText, maxInputLength)
        val inputBuffer = ByteBuffer.allocateDirect(maxInputLength * 4).apply {
            order(ByteOrder.nativeOrder())
            tokens.forEach { putInt(it) }
            while (position() < capacity()) putInt(0)
            rewind()
        }
        return inputBuffer
    }

    private fun prepareOutputBuffer(): ByteBuffer {
        val outputLength = 512
        return ByteBuffer.allocateDirect(outputLength * 4).apply {
            order(ByteOrder.nativeOrder())
            clear()
        }
    }

    private fun tokenizeInput(inputText: String, maxLength: Int): List<Int> {
        val words = inputText.split(" ").take(maxLength)
        return words.mapIndexed { index, _ -> index + 1 }.padEnd(maxLength, 0)
    }

    private fun decodeOutput(outputBuffer: ByteBuffer): String {
        outputBuffer.rewind()
        val outputTokens = IntArray(512)
        for (i in outputTokens.indices) {
            outputTokens[i] = outputBuffer.getInt()
        }
        return outputTokens.filter { it != 0 }
            .mapIndexed { index, _ -> "word${index + 1}" }
            .joinToString(" ")
    }

    private fun List<Int>.padEnd(length: Int, padValue: Int): List<Int> {
        return this + List(length - size) { padValue }
    }

    override fun close() {
        interpreter?.close()
        interpreter = null
        currentModelId = null
    }
}