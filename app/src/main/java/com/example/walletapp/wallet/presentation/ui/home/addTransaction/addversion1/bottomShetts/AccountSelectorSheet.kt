package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1.bottomShetts

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Account
import java.text.NumberFormat
import java.util.Locale

private fun parseColor(colorHex: String?): Color {
    return try {
        if (colorHex.isNullOrBlank() || !colorHex.startsWith("#")) {
            Color(0xFF8D6E63)
        } else {
            Color(android.graphics.Color.parseColor(colorHex))
        }
    } catch (e: IllegalArgumentException) {
        Color(0xFF8D6E63)
    }
}

@Composable
fun AccountSelectorSheet(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Hisobni tanlang",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
        )
        Divider(modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(accounts, key = { it.id }) { account ->
                AccountCardItem(
                    account = account,
                    isSelected = selectedAccount?.id == account.id,
                    onAccountClick = { onAccountSelected(account) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
@Composable
fun AccountCardItem(
    account: Account,
    isSelected: Boolean,
    onAccountClick: () -> Unit
) {
    val accountColor = parseColor(account.colorHex)
    val onSurface = MaterialTheme.colorScheme.onTertiary
    val surfaceColor = MaterialTheme.colorScheme.onBackground
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) accountColor.copy(alpha = 0.1f) else surfaceColor,
        label = "AccountContainerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accountColor else Color.Transparent,
        label = "AccountBorderColor"
    )
    val currencyFormatter = remember(account.initialBalance) {
        NumberFormat.getCurrencyInstance(Locale("uz", "UZ")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAccountClick() }
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accountColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = account.iconResId?:R.drawable.ic_naqd_pul),
                    contentDescription = null,
                    tint = accountColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Hisob nomi va Balans
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currencyFormatter.format(account.initialBalance),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }

            // 3. Tanlanganlik belgisi (Checkmark)
            if (isSelected) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Tanlangan",
                    tint = accountColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}