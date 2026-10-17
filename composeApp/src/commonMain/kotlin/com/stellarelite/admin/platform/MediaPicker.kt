package com.stellarelite.admin.platform

import androidx.compose.runtime.Composable

data class PickedMedia(val uri: String, val fileName: String)

enum class MediaSource { CAMERA, GALLERY, DOCUMENT }

@Composable
expect fun rememberMediaPicker(): (MediaSource, (PickedMedia?) -> Unit) -> Unit

/** 读取本地 uri 的原始字节（用于上传 KYC 证件），失败返回 null */
expect suspend fun readUriBytes(uri: String): ByteArray?

