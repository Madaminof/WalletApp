package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.manualColor
import dev.samandar.walletapp.ui.theme.qr_codeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onManualInput: () -> Unit,
    onQrScan: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.4f)) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Strings.title_add_transaction),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary
            )

            Text(
                text = stringResource(Strings.add_transaction_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionItem(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.edit_icon,
                    label = stringResource(Strings.method_manual),
                    description = stringResource(Strings.method_manual_desc),
                    color = manualColor,
                    onClick = {
                        onManualInput()
                        onDismiss()
                    }
                )
                SelectionItem(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.qr_code_icon,
                    label = stringResource(Strings.method_qr),
                    description = stringResource(Strings.method_qr_desc),
                    color = qr_codeColor,
                    onClick = {
                        onQrScan()
                        onDismiss()
                    }
                )
            }
        }
    }
}


@Composable
fun SelectionItem(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animatsiyasi
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(28.dp))
            .background(color.copy(alpha = 0.05f))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = color.copy(alpha = 0.2f)
                ),
                onClick = onClick
            )
            .border(
                width = 1.dp,
                color = if (isPressed) color.copy(0.5f) else color.copy(0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(vertical = 24.dp, horizontal = 12.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onTertiary
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
    }
}