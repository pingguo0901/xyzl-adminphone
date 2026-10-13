package com.stellarelite.admin.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberDatePickerLauncher(onDateSelected: (String) -> Unit): () -> Unit {
    return remember { { } }
}

