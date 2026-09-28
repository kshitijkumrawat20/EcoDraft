package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SpeechManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _audioAmplitudes = MutableStateFlow(List(12) { 0.2f })
    val audioAmplitudes: StateFlow<List<Float>> = _audioAmplitudes.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var timerJob: Job? = null
    private var simulationJob: Job? = null
    private var waveAnimationJob: Job? = null

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                            updateAmplitudes(normalized)
                        }
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {}
                        override fun onError(error: Int) {
                            Log.d("SpeechManager", "Speech recognition error code: $error")
                        }
                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                _liveTranscript.value = matches[0]
                            }
                        }
                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                _liveTranscript.value = matches[0]
                            }
                        }
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            } catch (e: Exception) {
                Log.e("SpeechManager", "Cannot create SpeechRecognizer", e)
            }
        }
    }

    fun startListening(sampleTextOverride: String? = null) {
        if (_isRecording.value) return
        _isRecording.value = true
        _elapsedSeconds.value = 0
        _liveTranscript.value = ""

        // Start timer
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _elapsedSeconds.value += 1
            }
        }

        // Start waveform ambient breathing
        waveAnimationJob?.cancel()
        waveAnimationJob = coroutineScope.launch {
            val basePattern = floatArrayOf(0.2f, 0.35f, 0.55f, 0.8f, 1.0f, 0.85f, 0.95f, 0.7f, 0.6f, 0.45f, 0.3f, 0.2f)
            var phase = 0f
            while (_isRecording.value) {
                delay(120)
                phase += 0.3f
                val dynamicList = basePattern.mapIndexed { i, base ->
                    val wave = kotlin.math.sin(phase + i * 0.45).toFloat() * 0.25f
                    (base + wave).coerceIn(0.12f, 1.0f)
                }
                _audioAmplitudes.value = dynamicList
            }
        }

        if (sampleTextOverride != null) {
            // Streaming simulation of speech
            simulationJob?.cancel()
            simulationJob = coroutineScope.launch {
                val words = sampleTextOverride.split(" ")
                val sb = StringBuilder()
                for (word in words) {
                    if (!_isRecording.value) break
                    delay((180..340).random().toLong())
                    if (sb.isNotEmpty()) sb.append(" ")
                    sb.append(word)
                    _liveTranscript.value = sb.toString()
                }
            }
            return
        }

        // Try device speech recognizer
        if (speechRecognizer != null) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("SpeechManager", "startListening failed, switching to bedside stream", e)
                startBedsideStreamFallback()
            }
        } else {
            startBedsideStreamFallback()
        }
    }

    private fun startBedsideStreamFallback() {
        simulationJob?.cancel()
        simulationJob = coroutineScope.launch {
            val defaultThoughts = listOf(
                "“I’m thinking about tomorrow’s presentation and why my stomach is in knots…”",
                "“Even though the slides are completed, my mind keeps looping on the Q&A section.”",
                "“Maybe I should read the conclusion aloud once before sleeping and turn off my screen.”"
            )
            val fullText = defaultThoughts.joinToString(" ")
            val words = fullText.split(" ")
            val sb = StringBuilder()
            for (word in words) {
                if (!_isRecording.value) break
                delay(220)
                if (sb.isNotEmpty()) sb.append(" ")
                sb.append(word)
                _liveTranscript.value = sb.toString()
            }
        }
    }

    private fun updateAmplitudes(peak: Float) {
        val base = floatArrayOf(0.15f, 0.3f, 0.5f, 0.75f, 1.0f, 0.85f, 0.95f, 0.65f, 0.5f, 0.4f, 0.25f, 0.15f)
        _audioAmplitudes.value = base.map { (it * peak).coerceIn(0.1f, 1.0f) }
    }

    fun stopListening(): String {
        _isRecording.value = false
        timerJob?.cancel()
        simulationJob?.cancel()
        waveAnimationJob?.cancel()
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}

        val transcript = _liveTranscript.value.trim()
        return if (transcript.isNotBlank()) transcript else "“I’m thinking about tomorrow’s presentation and why my stomach is in knots…”"
    }

    fun discard() {
        _isRecording.value = false
        timerJob?.cancel()
        simulationJob?.cancel()
        waveAnimationJob?.cancel()
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
        _liveTranscript.value = ""
        _elapsedSeconds.value = 0
    }

    fun destroy() {
        discard()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }
}
