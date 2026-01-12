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
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount

@Composable
fun PremiumAccountSelectionBox(
    selectedAccount: Account?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accColor = remember(selectedAccount?.colorHex) {
        try { Color(selectedAccount?.colorHex?.toColorInt() ?: 0xFF6200EE.toInt()) }
        catch (e: Exception) { Color(0xFF6200EE.toInt()) }
    }
    val accountName = getTranslatedName(selectedAccount?.name ?: stringResource(R.string.placeholder_select_account))

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon qismi
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        accColor.copy(0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = selectedAccount?.iconResId ?: R.drawable.ic_card_default),
                    contentDescription = null,
                    tint = Color.Unspecified,
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
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    lineHeight = 15.sp
                )
                if (selectedAccount != null) {
                    Text(
                        text = FormatAmount(selectedAccount.initialBalance),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        lineHeight = 9.sp
                    )
                }
            }
        }
    }
}