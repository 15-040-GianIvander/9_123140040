package com.gianivander.captiongenerator.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit

expect fun ByteArray.toImageBitmap(): ImageBitmap
