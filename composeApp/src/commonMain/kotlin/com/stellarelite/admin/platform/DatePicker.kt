package com.stellarelite.admin.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDatePickerLauncher(onDateSelected: (String) -> Unit): () -> Unit

