package dev.samandar.walletapp.wallet.presentation.ui.charts.iconHelper

import android.content.Context
import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.samandar.walletapp.R

@Composable
fun SafeCategoryIcon(
    iconName: String?, // Endi String qabul qiladi
    iconColor: Color,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val context = LocalContext.current
    val defaultIconId = R.drawable.default_category

    // Rasm ID sini hisoblaymiz (Caching qilib ketamiz)
    val resId = remember(iconName) {
        if (iconName.isNullOrEmpty()) {
            defaultIconId
        } else {
            val id = context.resources.getIdentifier(iconName, "drawable", context.packageName)
            if (id != 0) id else defaultIconId
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { imageView ->
            imageView.setColorFilter(iconColor.toArgb())
            imageView.setImageResource(resId)
        }
    )
}