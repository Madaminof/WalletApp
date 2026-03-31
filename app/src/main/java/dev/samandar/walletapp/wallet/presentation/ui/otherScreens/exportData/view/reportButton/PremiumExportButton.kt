package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.reportButton


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.samandar.walletapp.utils.Strings

@Composable
fun PremiumExportButton(
    isLoading: Boolean,
    onExportTypeSelected: (ExportFormat) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val transition = updateTransition(targetState = expanded, label = "PremiumMenu")

    val menuScale by transition.animateFloat(
        transitionSpec = {
            if (targetState) spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
            else spring(dampingRatio = 1f, stiffness = Spring.StiffnessMedium)
        }, label = "scale"
    ) { if (it) 1f else 0.8f }

    val menuAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "alpha"
    ) { if (it) 1f else 0f }

    val buttonColor by transition.animateColor(
        transitionSpec = { tween(300) }, label = "color"
    ) { state ->
        if (state) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.primary
    }

    val contentColor by transition.animateColor(
        transitionSpec = { tween(300) }, label = "contentColor"
    ) { state ->
        if (state) Color.Gray
        else MaterialTheme.colorScheme.onPrimary
    }

    val menuOffsetPx = with(LocalDensity.current) { (-88).dp.toPx() }.toInt()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .graphicsLayer {
                    val pressScale = if (expanded) 0.97f else 1f
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .shadow(
                    elevation = if (expanded) 4.dp else 16.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = MaterialTheme.colorScheme.primaryContainer
                ),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                expanded = !expanded
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = contentColor
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = contentColor,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateContentSize(spring(stiffness = Spring.StiffnessLow))
                ) {
                    val rotation by transition.animateFloat(label = "iconRotation") { state ->
                        if (state) 90f else 0f
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Rounded.Close else Icons.Rounded.IosShare,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotation),
                        tint = if (expanded) Color.Gray else MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(Modifier.width(12.dp))

                    AnimatedContent(
                        targetState = expanded,
                        transitionSpec = {
                            if (targetState) {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            } else {
                                (slideInVertically { -it } + fadeIn()) togetherWith
                                        (slideOutVertically { it } + fadeOut())
                            }.using(SizeTransform(clip = false))
                        },
                        label = "textChange"
                    ) { isExpanded ->
                        Text(
                            text = if (isExpanded) stringResource(Strings.btn_cancel)
                            else stringResource(Strings.export_action),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        if (expanded || transition.isRunning) {
            Popup(
                alignment = Alignment.BottomCenter,
                offset = IntOffset(0, menuOffsetPx),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true, dismissOnClickOutside = true)
            ) {
                val shadowAlpha by transition.animateFloat(label = "shadowAlpha") { if (it) 0.4f else 0f }

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = menuScale
                            scaleY = menuScale
                            alpha = menuAlpha
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        }
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color.Black.copy(alpha = shadowAlpha)
                        )
                ) {
                    Surface(
                        modifier = Modifier.width(300.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        tonalElevation = 8.dp,
                        border = BorderStroke(0.5.dp, Color.LightGray.copy(0.2f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = stringResource(Strings.export_select_format).uppercase(),
                                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 10.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.5.sp
                            )

                            ExportFormat.entries.forEach { format ->
                                TooltipItem(
                                    format = format,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        expanded = false
                                        onExportTypeSelected(format)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TooltipItem(
    format: ExportFormat,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = getFormatColor(format).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = format.icon,
                    contentDescription = null,
                    tint = getFormatColor(format),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // --- MATNLAR ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                )
                Text(
                    text = stringResource(id = format.descriptionRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
@Composable
fun getFormatColor(format: ExportFormat) = when (format) {
    ExportFormat.PDF -> Color(0xFFFF453A)
    ExportFormat.EXCEL -> Color(0xFF32D74B)
    ExportFormat.CSV -> Color(0xFF0A84FF)
}