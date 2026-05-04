package com.gianivander.captiongenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                geminiApiKey = BuildConfig.GEMINI_API_KEY
            )
        }
    }
}
