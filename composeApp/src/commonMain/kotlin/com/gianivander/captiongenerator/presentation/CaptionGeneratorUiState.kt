package com.gianivander.captiongenerator.presentation

data class CaptionGeneratorUiState(
    val inputPrompt: String = "",
    val selectedImageBytes: ByteArray? = null,
    val aiResult: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CaptionGeneratorUiState

        if (inputPrompt != other.inputPrompt) return false
        if (selectedImageBytes != null) {
            if (other.selectedImageBytes == null) return false
            if (!selectedImageBytes.contentEquals(other.selectedImageBytes)) return false
        } else if (other.selectedImageBytes != null) return false
        if (aiResult != other.aiResult) return false
        if (isLoading != other.isLoading) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputPrompt.hashCode()
        result = 31 * result + (selectedImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + aiResult.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}
