package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmountAccount

@Composable
fun PremiumAccountSelectionBox(
    selectedAccount: Account?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accColor = remember(selectedAccount?.colorHex) {
        try { Color(selectedAccount?.colorHex?.toColorInt() ?: 0xFF6200EE.toInt()) }
        catch (e: Exception) { Color(0xFF00838F) }
    }

    val accountName = getTranslatedName(selectedAccount?.name ?: stringResource(R.string.placeholder_select_account))

    val premiumShape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 14.dp,
        bottomStart = 14.dp
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),
        shape = premiumShape,
        color = defaultColor.copy(0.05f),
        border = BorderStroke(
            width = 0.8.dp,
            color = defaultColor.copy(0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = accColor.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = selectedAccount?.iconResId ?: R.drawable.ic_card_default),
                    contentDescription = null,
                    tint = accColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = accountName.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    letterSpacing = 0.3.sp,
                    lineHeight = 12.sp
                )
                if (selectedAccount != null) {
                    val color = if (selectedAccount.balance < 0) expenseColor else MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = FormatAmountAccount(selectedAccount.balance),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                        Text(
                            text = getCurrencySymbol(selectedAccount.currencyCode),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color =  color
                        )
                    }
                }
            }
        }
    }
}