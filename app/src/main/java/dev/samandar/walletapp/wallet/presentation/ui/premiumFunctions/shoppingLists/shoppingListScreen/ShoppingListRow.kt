package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists.shoppingListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency


@Composable
fun ShoppingListRow(
    list: ShoppingList,
    totalItems: Int,
    boughtItems: Int,
    totalAmount: Double,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (ShoppingList) -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isAllBought = totalItems > 0 && boughtItems == totalItems
    val colorLine = if (isAllBought) Color(0xFF4CAF50).copy(0.6f)
    else MaterialTheme.colorScheme.primary.copy(0.6f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onItemClick(list.id) },
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(colorLine)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isAllBought) Color(0xFF4CAF50).copy(0.1f)
                        else MaterialTheme.colorScheme.primary.copy(0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$boughtItems/$totalItems",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAllBought) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatAmountWithCurrency(totalAmount),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.share_icon),
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.list_row_edit_button), fontWeight = FontWeight.Medium) },
                            onClick = { onEdit(list); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.list_row_delete_button), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium) },
                            onClick = { onDelete(list.id); showMenu = false },
                            leadingIcon = { Icon(painter = painterResource(R.drawable.delete_icon), null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        }
    }
}