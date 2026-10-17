package com.stellarelite.admin.platform

/** 当前时间：yyyy-MM-dd 与 HH:mm */
expect fun currentTimeParts(): Pair<String, String>

/** 把文本内容保存为本机 PDF（下载/XYZL） */
expect fun saveStatementPdf(fileName: String, content: String): Boolean

/** 当前毫秒时间戳（用于上传文件名去重） */
expect fun nowMillis(): Long
