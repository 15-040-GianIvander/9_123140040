package com.gianivander.captiongenerator.data.repository

interface FinanceRepository {
    suspend fun generateContent(prompt: String, imageBytes: ByteArray? = null): Result<String>
}
