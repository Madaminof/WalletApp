package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account


@Composable
fun AccountFilterDialog(
    accounts: List<Account>,
    selectedAccountIds: Set<String>,
    onAccountSelectionChange: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val animatedOnDismiss = {
        isVisible = false
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            kotlinx.coroutines.delay(250)
            onDismiss()
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = animatedOnDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { animatedOnDismiss() }
                )
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it/2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(200)),
                exit = slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(28.dp)),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shadowElevation = 16.dp,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Divider(
                                modifier = Modifier
                                    .width(40.dp)
                                    .clip(CircleShape),
                                thickness = 4.dp,
                                color = Color.LightGray.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = stringResource(Strings.total_balance_title_dialog),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                            ),
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 350.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(accounts, key = { it.id }) { account ->
                                val isChecked = selectedAccountIds.contains(account.id)
                                FilterItemRow(
                                    account = account,
                                    isChecked = isChecked,
                                    onClick = { onAccountSelectionChange(account.id, !isChecked) }
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                onApply()
                                animatedOnDismiss()
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            enabled = selectedAccountIds.isNotEmpty(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                        ) {
                            Text(
                                text = "${stringResource(Strings.total_balance_save_button_txt)} (${selectedAccountIds.size}/${accounts.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterItemRow(
    account: Account,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isChecked) primaryAccent.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(account.colorHex ?: "#AAAAAA")))
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = account.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                color = if (isChecked) primaryAccent else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        )
        // Checkbox animatsiyasi (Ixtiyoriy)
        if (isChecked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .background(primaryAccent, CircleShape)
                    .padding(4.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, Color.LightGray.copy(0.5f), CircleShape)
            )
        }
    }
}