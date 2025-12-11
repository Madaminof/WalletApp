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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun DebtItemRow(
    debt: Debt,
    onToggleSettled: (Debt) -> Unit,
    onEdit: (Debt) -> Unit,
    onDelete: (Debt) -> Unit,
    onClick: (Debt) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    val lentColor = incomeColor
    val owedColor = expenseColor
    val settledColor = MaterialTheme.colorScheme.outline

    val actionColor = if (debt.isLent) lentColor else owedColor
    val indicatorColor = if (debt.isSettled) settledColor else actionColor

    val contentAlpha = if (debt.isSettled) 0.3f else 1.0f
    val textColor = if (debt.isSettled) settledColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(debt) }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .alpha(contentAlpha)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(indicatorColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (debt.isLent) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.person,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 15.sp,
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${if (debt.isLent) "Siz Berdingiz" else "Siz Oldingiz"} | ${dateFormatter.format(Date(debt.date))}",
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatAmountWithCurrency(debt.amount),
                    fontWeight = FontWeight.SemiBold,
                    color = actionColor,
                    fontSize = 13.sp,
                    lineHeight = 13.sp
                )

                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (debt.isSettled) "Qaytarilgan" else "Kutilmoqda",
                    fontWeight = FontWeight.Normal,
                    color = if (debt.isSettled) settledColor else actionColor,
                    fontSize = 11.sp,
                    lineHeight = 11.sp

                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Amallar", tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f))
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {

                    if (!debt.isSettled) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
                            onClick = { showMenu = false; onEdit(debt) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                    }
                    if (!debt.isSettled){
                        DropdownMenuItem(
                            text = { Text("Qaytarish", color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
                            onClick = { showMenu = false; onToggleSettled(debt) },
                            leadingIcon = { Icon(Icons.Default.CurrencyExchange, contentDescription = null) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = { showMenu = false; onDelete(debt) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}