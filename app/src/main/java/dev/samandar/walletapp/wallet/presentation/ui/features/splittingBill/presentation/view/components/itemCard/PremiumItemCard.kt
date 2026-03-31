package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.itemCard


import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.softShadow

@SuppressLint("DefaultLocale")
@Composable
fun PremiumItemCard(
    item: BillItemEntity,
    participants: List<ParticipantEntity>,
    assignments: List<ItemAssignmentEntity>,
    onToggleParticipant: (String) -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit,
    onDeleteItem: (String) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val selectedCount = assignments.size
    val totalPrice = item.price * item.quantity

    val animatedAlpha by animateFloatAsState(
        targetValue = if (selectedCount > 0) 1f else 0.8f, // Tanlanmaganda ancha shaffof bo'ladi
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "alpha"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (selectedCount > 0) 1f else 0.97f, // Kichrayish effekti chuqurlik beradi
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
            // 2. Soya Logikasi: Faqat tanlanganda paydo bo'ladi, tanlanmaganda "nol" bo'ladi
            .softShadow(
                color = if (selectedCount > 0)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    Color.Transparent, // Tanlanmaganda soya ranglar bilan aralashmaydi
                borderRadius = 30.dp,
                blurRadius = if (selectedCount > 0) 20.dp else 0.dp,
                offsetY = if (selectedCount > 0) 8.dp else 0.dp
            )
            // 3. Surface & Glassmorphism
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = if (selectedCount > 0) {
                    // Tanlanganda: Deep Luxury Gradient
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    )
                } else {
                    // Tanlanmaganda: O'ta shaffof "Oyna" effekti
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            )
            // 4. Dynamic Border (Professional Touch)
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = if (selectedCount > 0) {
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.05f)
                        )
                    }
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. Ixcham Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.quantity.toInt()} ${stringResource(Strings.unit_quantity)} • ${
                            String.format(
                                "%,.0f",
                                item.price
                            )
                        } so'm",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDeleteItem(item.id)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        null,
                        Modifier.size(20.dp),
                        MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Minimalistik Narx Paneli
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.6f))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceDisplay(
                        stringResource(Strings.title_total),
                        totalPrice,
                        MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )

                    if (selectedCount > 0) {
                        VerticalDivider(
                            modifier = Modifier
                                .height(24.dp)
                                .width(1.dp)
                                .alpha(0.2f)
                        )
                        PriceDisplay(
                            label = stringResource(Strings.per_person),
                            price = totalPrice / selectedCount,
                            color = MaterialTheme.colorScheme.primary,
                            isBold = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Ishtirokchilar - Zamonaviy Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (selectedCount == 0) stringResource(Strings.select_participant) else "$selectedCount ${
                        stringResource(
                            Strings.participants_assigned
                        )
                    }",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedCount == 0) MaterialTheme.colorScheme.error.copy(0.7f) else MaterialTheme.colorScheme.primary
                )

                // Smart "All" Toggle
                val isAll = selectedCount == participants.size
                Icon(
                    imageVector = if (isAll) Icons.Rounded.Close else Icons.Rounded.GroupAdd,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isAll) onClearAll() else onSelectAll()
                        }
                        .padding(6.dp),
                    tint = if (isAll) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                participants.forEach { p ->
                    val isSelected = assignments.any { it.participantId == p.id }
                    CompactParticipantChip(
                        name = p.name,
                        isSelected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            onToggleParticipant(p.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceDisplay(label: String, price: Double, color: Color, isBold: Boolean = false) {
    Column {
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = color.copy(alpha = 0.7f),
            letterSpacing = 1.sp
        )
        Text(
            "${String.format("%,.0f", price)} so'm",
            fontSize = if (isBold) 15.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CompactParticipantChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(if (isSelected) 1f else 0.95f, label = "scale")
    val alpha by animateFloatAsState(if (isSelected) 1f else 0.6f, label = "alpha")

    Surface(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(0.4f) else Color.Gray.copy(
            0.1f
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    Modifier.size(14.dp),
                    MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(0.3f))
                )
            }
            Text(
                name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary.copy(
                    0.4f
                )
            )
        }
    }
}

