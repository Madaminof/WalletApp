package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.presentation.ui.budjets.PremiumCustomLinearProgressIndicator
import dev.samandar.walletapp.wallet.presentation.ui.budjets.editBudget.EditBudgetSheet
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.FormatDate
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs


@Composable
fun BudgetRowItem(
    status: BudgetStatus,
    index: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryName = getTranslatedName(status.budget.category.name)
    val iconTxtColor =  MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 15L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutQuart), label = "alpha"
    )
    val translateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "y"
    )

    val dateFormatter = remember { SimpleDateFormat("dd-MMM, HH:mm", Locale.getDefault()) }
    val createdDateString = remember(status.budget.createdAt) {
        dateFormatter.format(Date(status.budget.createdAt))
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    val progress = (status.percentageUsed / 100).toFloat().coerceIn(0f, 1f)
    val progressFloat by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    val remaining = remember(status.budget.maxAmount, status.spentAmount) {
        status.budget.maxAmount - status.spentAmount
    }
    val progressColor = remember(remaining, progress) {
        when {
            remaining < 0 -> Color(0xFFE57373)
            progress >= 0.9f -> Color(0xFFFFB74D)
            else -> Color(0xFF81C784)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = translateY
            }
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
        tonalElevation = 12.dp
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(status.budget.category.colorArgb).copy(0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = status.budget.category.iconResId?: R.drawable.ic_wallet_2),
                        contentDescription = stringResource(R.string.budget_icon_content_description_category),
                        tint = Color(status.budget.category.colorArgb),
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoryName.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = iconTxtColor
                        )
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                                    .border(0.5.dp, Color.LightGray.copy(0.2f), RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.action_delete),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp
                                            )
                                        )
                                    },
                                    leadingIcon = { Icon(painter = painterResource(R.drawable.delete_icon), null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },

                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.error,
                                        leadingIconColor = MaterialTheme.colorScheme.error
                                    )
                                )
                            }

                        }
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append("${stringResource(R.string.budget_label_limit)}  ")
                            }
                            append(FormatAmount(status.budget.maxAmount))
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append("${stringResource(R.string.budget_label_spent)}  ")
                            }
                            append(FormatAmount(status.spentAmount))
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append("(${FormatDate(status.budget.startDate)})")
                            status.budget.endDate?.let { endDate ->
                                append(" - (${FormatDate(endDate)})")
                            }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (remaining >= 0) stringResource(R.string.budget_label_remaining)
                            else stringResource(R.string.budget_label_over_budget),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (remaining >= 0) MaterialTheme.colorScheme.onTertiary.copy(0.7f) else MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                        Text(
                            text = FormatAmount(abs(remaining)),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (remaining >= 0) MaterialTheme.colorScheme.onTertiary.copy(0.7f) else MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    PremiumCustomLinearProgressIndicator(
                        progressFloat = progressFloat,
                        progressColor = progressColor,
                        trackColor = MaterialTheme.colorScheme.onTertiary.copy(0.01f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )

                    Text(
                        text = "${stringResource(R.string.budget_label_created_at)}: $createdDateString",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.End
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, end = 4.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirmDelete = {
                    showDeleteDialog = false
                    onDelete()
                },
                title = stringResource(R.string.budget_delete_dialog_title),
                text = stringResource(R.string.budget_delete_dialog_text)
            )
        }
    }
}