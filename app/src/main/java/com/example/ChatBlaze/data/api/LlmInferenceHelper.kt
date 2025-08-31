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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class LlmInferenceHelper(
    private val context: Context,
    private val modelDownloaderViewModel: ModelDownloaderViewModel
) : AutoCloseable {
    private var interpreter: Interpreter? = null
    private var currentModel: Model? = null
    private var isInterpreterReady = false

    suspend fun loadModelAndPrepareInterpreter(modelId: String) {
        if (currentModel?.id == modelId && isInterpreterReady) {
            Log.d("LlmInferenceHelper", "Interpreter for $modelId is already loaded.")
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
                    setNumThreads(4)
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

        val inputTensor = interpreter!!.getInputTensor(0)
        val maxInputLength = inputTensor.shape()[1]
        var currentTokens = tokenizeInput(inputText, maxInputLength)

        val maxOutputTokens = 100
        val eosToken = -1

        for (i in 0 until maxOutputTokens) {
            val inputBuffer = prepareInput(currentTokens, maxInputLength)
            val outputs = mutableMapOf<Int, Any>()
            val outputTensor = interpreter!!.getOutputTensor(0)
            val outputBuffer = ByteBuffer.allocateDirect(outputTensor.numBytes()).order(ByteOrder.nativeOrder())
            outputs[0] = outputBuffer

            val inputs = arrayOf<Any>(inputBuffer)
            interpreter!!.runForMultipleInputsOutputs(inputs, outputs)

            val outputLogits = (outputs[0] as ByteBuffer).asFloatBuffer()
            val nextToken = argmax(outputLogits)

            if (nextToken == eosToken) {
                break
            }

            currentTokens = currentTokens.drop(1) + nextToken

            val decodedWord = decodeOutput(nextToken)
            emit("$decodedWord ")
            delay(50)
        }
    }
        .flowOn(Dispatchers.Default) // <-- THIS IS THE FIX

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

    private fun prepareInput(tokens: List<Int>, maxInputLength: Int): ByteBuffer {
        val paddedTokens = tokens.padEnd(maxInputLength, 0)
        val byteBufferSize = maxInputLength * 4
        return ByteBuffer.allocateDirect(byteBufferSize).apply {
            order(ByteOrder.nativeOrder())
            paddedTokens.forEach { putInt(it) }
            rewind()
        }
    }

    private fun tokenizeInput(inputText: String, maxLength: Int): List<Int> {
        val words = inputText.split(" ").take(maxLength)
        return words.mapIndexed { index, _ -> index + 1 }
    }

    private fun decodeOutput(token: Int): String {
        return "word${token}"
    }

    private fun argmax(buffer: FloatBuffer): Int {
        var maxVal = -Float.MAX_VALUE
        var maxIdx = -1
        buffer.rewind()
        for (i in 0 until buffer.remaining()) {
            val currentVal = buffer.get(i)
            if (currentVal > maxVal) {
                maxVal = currentVal
                maxIdx = i
            }
        }
        return maxIdx
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