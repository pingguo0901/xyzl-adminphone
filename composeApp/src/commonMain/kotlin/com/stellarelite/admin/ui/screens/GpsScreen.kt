package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.stellarelite.admin.ui.components.EmptyView
import com.stellarelite.admin.ui.components.PageTitle

@Composable
fun GpsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        PageTitle("GPS")
        EmptyView("GPS 功能开发中，敬请期待")
    }
}
