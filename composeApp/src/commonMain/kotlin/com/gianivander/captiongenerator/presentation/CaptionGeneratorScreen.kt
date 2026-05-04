package com.gianivander.captiongenerator.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gianivander.captiongenerator.utils.rememberImagePicker
import com.gianivander.captiongenerator.utils.toImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptionGeneratorScreen(
    viewModel: CaptionGeneratorViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val imagePicker = rememberImagePicker { bytes ->
        viewModel.onImageSelected(bytes)
    }

    val primaryBlue = Color(0xFF2196F3)
    val lightBlueBackground = Color(0xFFF0F7FF)
    val errorBackground = Color(0xFFFFEBEE)
    val errorText = Color(0xFFD32F2F)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Caption Generator",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryBlue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = lightBlueBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Apa yang ingin kamu posting?",
                        color = primaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Image Picker / Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .clickable(enabled = uiState.selectedImageBytes == null) {
                                imagePicker()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.selectedImageBytes != null) {
                            uiState.selectedImageBytes?.toImageBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            
                            // Delete Button
                            IconButton(
                                onClick = { viewModel.removeImage() },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("📷", fontSize = 40.sp)
                                Text(
                                    "Ketuk untuk tambah foto",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.inputPrompt,
                        onValueChange = viewModel::onPromptChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Masukkan konteks postingan...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Button(
                        onClick = { viewModel.generateCaption() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Generate Caption & Penilaian ✨", color = Color.White)
                        }
                    }
                }
            }

            // Error Message
            uiState.errorMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = errorBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = message,
                            color = errorText,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🔄", fontSize = 18.sp)
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }

            // AI Result Display
            if (uiState.aiResult.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Hasil Analisis AI ✨",
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                        Text(uiState.aiResult)
                    }
                }
            }
        }
    }
}
