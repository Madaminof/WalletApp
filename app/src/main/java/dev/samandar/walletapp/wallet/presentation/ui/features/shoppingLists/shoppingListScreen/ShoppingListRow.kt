package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListScreen

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import kotlinx.coroutines.delay


@Composable
fun ShoppingListRow(
    list: ShoppingList,
    totalItems: Int,
    boughtItems: Int,
    totalAmount: Double,
    index: Int,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (ShoppingList) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isAllBought = totalItems > 0 && boughtItems == totalItems
    val progress = if (totalItems > 0) boughtItems.toFloat() / totalItems else 0f

    val accentColor = if (isAllBought) incomeColor else MaterialTheme.colorScheme.primary
    val cardBackground = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
    val iconTxtColor =  MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 15L)
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = EaseOutQuart), label = ""
    )
    val slideUp by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessLow), label = ""
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = slideUp
            }
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(list.id) },
        color = cardBackground,
        tonalElevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round,

                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = accentColor,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.shopp_list_ic),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = list.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.4).sp
                        ),
                        color = iconTxtColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$boughtItems / $totalItems ${stringResource(R.string.items_label)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.offset(x = 12.dp, y = 4.dp).size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = formatAmountWithCurrency(totalAmount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = iconTxtColor
                        )
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.End)) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(cardBackground)
                        .border(0.5.dp, Color.LightGray.copy(0.2f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.list_row_edit_button), fontSize = 15.sp, color = iconTxtColor) },
                        leadingIcon = { Icon(painter = painterResource(R.drawable.edit2_icon), null, modifier = Modifier.size(18.dp), tint = iconTxtColor) },
                        onClick = { onEdit(list); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.list_row_share_button), fontSize = 15.sp,color = iconTxtColor) },
                        leadingIcon = { Icon(painter = painterResource(R.drawable.share_icon), null, modifier = Modifier.size(18.dp),tint = iconTxtColor) },
                        onClick = { onShare(); showMenu = false }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(0.1f))
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.list_row_delete_button), color = Color.Red, fontSize = 15.sp) },
                        leadingIcon = { Icon(painter = painterResource(R.drawable.delete_icon), null, tint = Color.Red, modifier = Modifier.size(18.dp)) },
                        onClick = { onDelete(list.id); showMenu = false }
                    )
                }
            }
        }
    }
}