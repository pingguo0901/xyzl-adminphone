package com.stellarelite.admin.platform

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun rememberMediaPicker(): (MediaSource, (PickedMedia?) -> Unit) -> Unit {
    val context = LocalContext.current
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCallback by remember { mutableStateOf<((PickedMedia?) -> Unit)?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val cb = pendingCallback
        val uri = pendingCameraUri
        pendingCallback = null
        pendingCameraUri = null
        cb?.invoke(if (ok && uri != null) PickedMedia(uri.toString(), "camera_photo.jpg") else null)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cb = pendingCallback
        pendingCallback = null
        cb?.invoke(uri?.let { PickedMedia(it.toString(), queryDisplayName(context, it) ?: "gallery_image") })
    }
    val docLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val cb = pendingCallback
        pendingCallback = null
        cb?.invoke(uri?.let { PickedMedia(it.toString(), queryDisplayName(context, it) ?: "document") })
    }

    return { source, onPicked ->
        pendingCallback = onPicked
        when (source) {
            MediaSource.CAMERA -> {
                val file = File(context.cacheDir, "cam_" + System.currentTimeMillis() + ".jpg")
                val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                pendingCameraUri = uri
                cameraLauncher.launch(uri)
            }
            MediaSource.GALLERY -> galleryLauncher.launch("image/*")
            MediaSource.DOCUMENT -> docLauncher.launch(
                arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "image/*"
                )
            )
        }
    }
}

actual suspend fun readUriBytes(uri: String): ByteArray? {
    val context = AdminAppContext.context ?: return null
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    return try {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    } catch (e: Exception) {
        null
    }
}

