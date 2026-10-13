package com.stellarelite.admin.platform

import androidx.compose.runtime.Composable

data class PickedMedia(val uri: String, val fileName: String)

enum class MediaSource { CAMERA, GALLERY, DOCUMENT }

@Composable
expect fun rememberMediaPicker(): (MediaSource, (PickedMedia?) -> Unit) -> Unit

