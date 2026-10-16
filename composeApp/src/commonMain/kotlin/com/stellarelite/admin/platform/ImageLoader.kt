package com.stellarelite.admin.platform

import androidx.compose.ui.graphics.ImageBitmap

/** 把本机图片 uri（相册 / 拍照返回）解码成 ImageBitmap；失败返回 null */
expect fun loadImageBitmap(uri: String): ImageBitmap?
