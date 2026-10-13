package com.stellarelite.admin.platform

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale

@Composable
actual fun rememberDatePickerLauncher(onDateSelected: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    return remember {
        {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onDateSelected(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                    )
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}

