package com.gianivander.captiongenerator

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gianivander.captiongenerator.data.remote.GeminiService
import com.gianivander.captiongenerator.data.repository.FinanceRepositoryImpl
import com.gianivander.captiongenerator.presentation.CaptionGeneratorScreen
import com.gianivander.captiongenerator.presentation.CaptionGeneratorViewModel

@Composable
fun App(
    geminiApiKey: String
) {
    MaterialTheme {
        val viewModel = remember {
            val geminiService = GeminiService(
                apiKey = geminiApiKey
            )

            val financeRepository = FinanceRepositoryImpl(
                geminiService = geminiService
            )

            CaptionGeneratorViewModel(
                repository = financeRepository
            )
        }

        CaptionGeneratorScreen(
            viewModel = viewModel
        )
    }
}
