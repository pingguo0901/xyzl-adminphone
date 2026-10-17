package com.stellarelite.admin.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.writeToFile

actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun currentTimeParts(): Pair<String, String> {
    val fmt = NSDateFormatter().apply { dateFormat = "yyyy-MM-dd" }
    val fmtTime = NSDateFormatter().apply { dateFormat = "HH:mm" }
    val now = NSDate()
    return fmt.stringFromDate(now) to fmtTime.stringFromDate(now)
}

actual fun saveStatementPdf(fileName: String, content: String): Boolean {
    return try {
        val dir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).firstOrNull() as? String
            ?: return false
        val path = dir + "/" + fileName
        (content as NSString).writeToFile(path, true, NSUTF8StringEncoding, null)
    } catch (_: Exception) {
        false
    }
}
