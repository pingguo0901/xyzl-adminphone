package com.stellarelite.admin.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberMediaPicker(): (MediaSource, (PickedMedia?) -> Unit) -> Unit {
    return remember { { _, _ -> } }
}

actual suspend fun readUriBytes(uri: String): ByteArray? = null

