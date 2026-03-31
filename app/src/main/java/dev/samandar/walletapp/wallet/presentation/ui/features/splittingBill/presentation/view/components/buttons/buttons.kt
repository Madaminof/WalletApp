package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.buttons

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings

// ContextWrapper ichidan haqiqiy Activity topuvchi extension
// Bu ilovadan chiqib ketish (crash) muammosini hal qiladi
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onPdfExport: () -> Unit, // Yangi funksiya
    isSaving: Boolean = false,
    isValid: Boolean = true,
) {
    val context = LocalContext.current

    // Ranglar palitrasi
    val disabledColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
    val disabledContent = MaterialTheme.colorScheme.onTertiary.copy(0.3f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. PDF Tugmasi (Premium ko'rinish)
            IconButtonVariant(
                onClick = onPdfExport,
                enabled = isValid,
                icon = Icons.Rounded.PictureAsPdf,
                modifier = Modifier.weight(0.6f)
            )

            // 2. Share Tugmasi
            IconButtonVariant(
                onClick = onShare,
                enabled = isValid,
                icon = Icons.Rounded.Share,
                modifier = Modifier.weight(0.6f)
            )

            // 3. Save Tugmasi (Asosiy harakat)
            Button(
                onClick = onSave,
                enabled = isValid && !isSaving,
                modifier = Modifier
                    .height(54.dp)
                    .weight(1.8f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = disabledColor,
                    disabledContentColor = disabledContent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        stringResource(Strings.button_save),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun IconButtonVariant(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer.copy(0.7f) else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.primary.copy(0.3f) else MaterialTheme.colorScheme.onTertiary.copy(0.1f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}