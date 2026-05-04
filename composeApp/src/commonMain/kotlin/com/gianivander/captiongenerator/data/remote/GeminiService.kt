package com.gianivander.captiongenerator.data.remote

import com.gianivander.captiongenerator.data.model.GeminiContent
import com.gianivander.captiongenerator.data.model.GeminiInlineData
import com.gianivander.captiongenerator.data.model.GeminiPart
import com.gianivander.captiongenerator.data.model.GeminiRequest
import com.gianivander.captiongenerator.data.model.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class GeminiService(
    private val apiKey: String
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.5-flash-lite"

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun generateContent(prompt: String, imageBytes: ByteArray? = null): Result<String> {
        return try {
            if (apiKey.isBlank() || !apiKey.startsWith("AIza")) {
                return Result.failure(
                    Exception("Gemini API key tidak valid. Periksa local.properties atau gradle.properties.")
                )
            }

            val parts = mutableListOf<GeminiPart>()
            parts.add(GeminiPart(text = prompt))
            
            imageBytes?.let {
                val base64Image = Base64.encode(it)
                parts.add(
                    GeminiPart(
                        inlineData = GeminiInlineData(
                            mimeType = "image/jpeg",
                            data = base64Image
                        )
                    )
                )
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = parts
                    )
                )
            )

            val httpResponse = client.post(
                "$baseUrl/models/$model:generateContent"
            ) {
                contentType(ContentType.Application.Json)
                parameter("key", apiKey)
                setBody(request)
            }

            val rawResponse = httpResponse.bodyAsText()
            val statusCode = httpResponse.status.value

            if (statusCode !in 200..299) {
                return Result.failure(Exception("Gemini API error $statusCode: $rawResponse"))
            }

            val response = json.decodeFromString<GeminiResponse>(rawResponse)
            val candidate = response.candidates.firstOrNull()

            if (candidate == null) {
                return Result.failure(Exception("Tidak ada respon dari Gemini."))
            }

            val resultText = candidate.content?.parts
                ?.mapNotNull { it.text }
                ?.joinToString("\n")
                ?.trim()

            if (resultText.isNullOrBlank()) {
                Result.failure(Exception("Respon kosong dari Gemini."))
            } else {
                Result.success(resultText)
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan koneksi."))
        }
    }
}
