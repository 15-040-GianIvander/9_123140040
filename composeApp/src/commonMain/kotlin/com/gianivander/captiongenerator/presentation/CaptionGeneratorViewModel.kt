package com.gianivander.captiongenerator.presentation

import com.gianivander.captiongenerator.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CaptionGeneratorViewModel(
    private val repository: FinanceRepository
) {
    private val _uiState = MutableStateFlow(CaptionGeneratorUiState())
    val uiState: StateFlow<CaptionGeneratorUiState> = _uiState.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    fun onPromptChanged(value: String) {
        _uiState.update { it.copy(inputPrompt = value, errorMessage = null) }
    }

    fun onImageSelected(bytes: ByteArray?) {
        _uiState.update { it.copy(selectedImageBytes = bytes) }
    }

    fun removeImage() {
        _uiState.update { it.copy(selectedImageBytes = null) }
    }

    fun generateCaption() {
        val currentState = _uiState.value
        if (currentState.inputPrompt.isBlank() && currentState.selectedImageBytes == null) {
            _uiState.update { it.copy(errorMessage = "Masukkan teks atau tambahkan foto.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, aiResult = "", errorMessage = null) }

        val customPrompt = """
            Tugasmu adalah membuat caption media sosial yang menarik dan memberikan penilaian/insight untuk postingan berikut.
            Postingan: ${currentState.inputPrompt}
            
            Format jawaban:
            1. Rekomendasi Caption (berikan 3 pilihan: Lucu, Professional, dan Singkat)
            2. Penilaian Konten
            3. Rekomendasi Hashtag
        """.trimIndent()

        viewModelScope.launch {
            repository.generateContent(customPrompt, currentState.selectedImageBytes)
                .onSuccess { result ->
                    _uiState.update { it.copy(aiResult = result, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
        }
    }

    fun retry() {
        generateCaption()
    }
}
