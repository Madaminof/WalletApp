package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType

@Composable
fun TransactionTypeTabRowPremium(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    val tabs = listOf(
        TransactionType.EXPENSE to stringResource(Strings.type_expense_value),
        TransactionType.INCOME to stringResource(Strings.type_income_value),
    )
    val amountColor = if (selected == TransactionType.INCOME) incomeColor else expenseColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { (type, title) ->
            val isSelected = selected == type
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onSelect(type)
                        SoundManager.playClick()
                    }
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onTertiary else Color.Gray.copy(alpha = 0.6f)
                    )
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(amountColor, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}