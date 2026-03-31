package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.viewmodel.SplitBillUiState
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.viewmodel.SplitBillViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParticipantsBlock(
    state: SplitBillUiState,
    viewModel: SplitBillViewModel,
) {
    // FlowRow orqali ishtirokchilarni chiroyli joylashtiramiz
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.participants.forEach { participant ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = spring()) + scaleIn(initialScale = 0.85f),
                exit = fadeOut() + scaleOut()
            ) {
                LuxuryParticipantChip(
                    name = participant.name,
                    onRemove = { viewModel.removeParticipant(participant.id) }
                )
            }
        }
    }
}

@Composable
fun LuxuryParticipantChip(
    name: String,
    onRemove: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .softShadow(
                color = Color.Black.copy(alpha = 0.06f), // O'ta yumshoq soya
                borderRadius = 32.dp,
                blurRadius = 10.dp,
                offsetY = 4.dp
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimaryContainer) // Glassmorphism
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Mono-Avatar (Minimalist chuqurlik bilan)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.04f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                    )
                )
            }

            // 2. Ishtirokchi ismi
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                modifier = Modifier.padding(end = 4.dp)
            )

            // 3. O'chirish tugmasi (Minimalist & Functional)
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRemove()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * Premium Soft Shadow Modifikatori
 * Bu funksiya elementga tabiiy chuqurlik va suzish effektini beradi.
 */
fun Modifier.softShadow(
    color: Color = Color.Black.copy(alpha = 0.1f),
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 8.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 0.dp
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        // Soyaning rangi va bluri
        frameworkPaint.color = Color.Transparent.toArgb()
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            color.toArgb()
        )

        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}