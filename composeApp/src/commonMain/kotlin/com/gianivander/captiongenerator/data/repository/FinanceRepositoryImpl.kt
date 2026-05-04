package com.gianivander.captiongenerator.data.repository

import com.gianivander.captiongenerator.data.remote.GeminiService

class FinanceRepositoryImpl(
    private val geminiService: GeminiService
) : FinanceRepository {

    override suspend fun generateContent(prompt: String, imageBytes: ByteArray?): Result<String> {
        return geminiService.generateContent(prompt, imageBytes)
    }
}
