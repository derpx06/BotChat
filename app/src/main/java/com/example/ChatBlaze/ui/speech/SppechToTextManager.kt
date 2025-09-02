package com.example.ChatBlaze.ui.speech

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpeechToTextManager(
    private val application: Application
) {
    private val _state = MutableStateFlow(SpeechState())
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    private val recognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    init {
        setupListener()
    }

    fun startListening(languageCode: String = "en-US") {
        if (!SpeechRecognizer.isRecognitionAvailable(application)) {
            _state.update { it.copy(error = "Speech recognition is not available.") }
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        recognizer.startListening(intent)
        _state.update { it.copy(isRecording = true) }
    }

    fun stopListening() {
        recognizer.stopListening()
        _state.update { it.copy(isRecording = false) }
    }

    private fun setupListener() {
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.update { it.copy(error = null) }
            }

            override fun onBeginningOfSpeech() {
                // UI can react to the user starting to speak
            }

            override fun onRmsChanged(rmsdB: Float) {
                // You can use this to create a visualizer for the mic
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _state.update { it.copy(isRecording = false) }
            }

            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                    return // Ignore common non-fatal errors
                }
                _state.update { it.copy(error = "Error: $error", isRecording = false) }
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.let { result ->
                        _state.update { it.copy(spokenText = result) }
                    }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.let { partialResult ->
                        _state.update { it.copy(spokenText = partialResult) }
                    }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    data class SpeechState(
        val spokenText: String = "",
        val isRecording: Boolean = false,
        val error: String? = null
    )
}
