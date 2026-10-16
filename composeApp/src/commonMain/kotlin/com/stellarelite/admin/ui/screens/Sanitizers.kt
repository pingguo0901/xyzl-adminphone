package com.stellarelite.admin.ui.screens

/** 马来西亚区号选项（复刻司机端） */
internal val DialCodes = listOf("+60", "+65", "+62", "+66", "+86", "+852", "+853", "+886")

/** 性别选项 */
internal val GenderOptions = listOf("男", "女")

/** 昵称：中文 / 英文 / 数字 / 常见标点 */
internal fun sanitizeNickname(input: String): String =
    input.filter { it.isLetterOrDigit() || it in " _-.·,，。！？!?@#&()（）【】" }

/** 姓名：仅英文和空格 */
internal fun sanitizeEnglishName(input: String): String =
    input.filter { (it.isLetter() && it.code < 128) || it == ' ' }

/** 英文数字标点：门牌 / 花园 */
internal fun sanitizeEnglishPunct(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in " ,.-/#()'" }

/** 纯数字 */
internal fun sanitizeDigits(input: String): String = input.filter { it.isDigit() }

/** 纯英文 */
internal fun sanitizeEnglish(input: String): String =
    input.filter { (it.isLetter() && it.code < 128) || it == ' ' }

/** 邮箱：英文 + 数字 + 邮箱标点 */
internal fun sanitizeEmail(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in "._%+-@" }

/** 微信号：英文 + 数字 + 标点 */
internal fun sanitizeWechat(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in "._-@" }

/** 密码：英文 / 数字 / 常用符号（至少 8 位由页面校验） */
internal fun sanitizePassword(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in "!@#$%^&*()_-+=.,?" }

/** 车牌：大写英文数字 + 空格 */
internal fun sanitizePlate(input: String): String =
    input.uppercase().filter { (it.isLetterOrDigit() && it.code < 128) || it == ' ' }
