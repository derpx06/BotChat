package com.example.ChatBlaze.data.api

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.ChatBlaze.data.downlaod.Model
import com.example.ChatBlaze.data.downlaod.ModelDownloaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class LlmInferenceHelper(
    private val context: Context,
    private val modelDownloaderViewModel: ModelDownloaderViewModel
) : AutoCloseable {
    private var interpreter: Interpreter? = null
    private var currentModel: Model? = null
    private var isInterpreterReady = false

    suspend fun loadModelAndPrepareInterpreter(modelId: String) {
        if (currentModel?.id == modelId && isInterpreterReady) {
            Log.d("LlmInferenceHelper", "Interpreter for $modelId is already loaded and ready.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                isInterpreterReady = false
                interpreter?.close()

                Log.d("LlmInferenceHelper", "Loading model file for $modelId...")
                val modelFile = getModelFile(modelId)

                Log.d("LlmInferenceHelper", "Initializing TFLite interpreter for $modelId...")
                val options = Interpreter.Options().apply {
                    addDelegate(org.tensorflow.lite.nnapi.NnApiDelegate())
                }
                interpreter = Interpreter(modelFile, options)

                currentModel = modelDownloaderViewModel.uiState.value.models.find { it.id == modelId }
                isInterpreterReady = true
                Log.d("LlmInferenceHelper", "Successfully loaded interpreter for $modelId.")
            } catch (e: Exception) {
                isInterpreterReady = false
                Log.e("LlmInferenceHelper", "Failed to load model $modelId", e)
                throw e
            }
        }
    }

    fun generateResponseStream(inputText: String): Flow<String> = flow {
        if (!isInterpreterReady || interpreter == null) {
            throw IllegalStateException("Interpreter is not ready. Call loadModelAndPrepareInterpreter first.")
        }

        val inputBuffer = prepareInput(inputText)

        val outputs = mutableMapOf<Int, Any>()
        val outputTensor = interpreter!!.getOutputTensor(0)
        val outputBuffer = ByteBuffer.allocateDirect(outputTensor.numBytes())
            .order(ByteOrder.nativeOrder())
        outputs[0] = outputBuffer

        val inputs = arrayOf<Any>(inputBuffer)

        interpreter!!.runForMultipleInputsOutputs(inputs, outputs)

        val fullResponse = decodeOutput(outputs[0] as ByteBuffer)

        fullResponse.split(" ").forEach { word ->
            emit("$word ")
            delay(50)
        }
    }

    private fun getModelFile(modelId: String): File {
        val model = modelDownloaderViewModel.uiState.value.models.find { it.id == modelId }
            ?: throw IllegalStateException("Model metadata for $modelId not found.")

        val extension = model.url.substringAfterLast('.', "bin")
        val fileName = "$modelId.$extension"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        val modelFile = File(dir, fileName)

        if (!modelFile.exists()) {
            throw IllegalStateException("Model file not found at: ${modelFile.absolutePath}")
        }
        return modelFile
    }

    private fun prepareInput(inputText: String): ByteBuffer {
        val inputTensor = interpreter?.getInputTensor(0) ?: throw IllegalStateException("Interpreter not loaded")
        val inputShape = inputTensor.shape()
        val maxInputLength = inputShape.getOrElse(1) { 0 }

        if (maxInputLength == 0) throw IllegalStateException("Invalid input tensor shape: ${inputShape.joinToString()}")

        val tokens = tokenizeInput(inputText, maxInputLength)

        val byteBufferSize = maxInputLength * inputTensor.dataType().byteSize()
        return ByteBuffer.allocateDirect(byteBufferSize).apply {
            order(ByteOrder.nativeOrder())
            tokens.forEach { putInt(it) }
            rewind()
        }
    }

    private fun tokenizeInput(inputText: String, maxLength: Int): List<Int> {
        val words = inputText.split(" ").take(maxLength)
        return words.mapIndexed { index, _ -> index + 1 }.padEnd(maxLength, 0)
    }

    private fun decodeOutput(outputBuffer: ByteBuffer): String {
        outputBuffer.rewind()
        val outputTensor = interpreter?.getOutputTensor(0) ?: throw IllegalStateException("Interpreter not loaded")
        val outputLength = outputTensor.shape()[1]

        val outputTokens = IntArray(outputLength)
        outputBuffer.asIntBuffer().get(outputTokens)

        return outputTokens.filter { it != 0 }
            .mapIndexed { index, _ -> "word${index + 1}" }
            .joinToString(" ")
    }

    private fun List<Int>.padEnd(length: Int, padValue: Int): List<Int> {
        if (this.size >= length) return this.take(length)
        return this + List(length - this.size) { padValue }
    }

    override fun close() {
        interpreter?.close()
        interpreter = null
        currentModel = null
        isInterpreterReady = false
    }
}