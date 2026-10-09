package com.stellarelite.admin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stellarelite.admin.R

@Composable
actual fun AppIcon(
    modifier: Modifier,
    size: Int
) {
    Image(
        painter = painterResource(id = R.drawable.app_icon),
        contentDescription = "星域臻旅 后台端",
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.22).dp)),
        contentScale = ContentScale.Crop
    )
}
