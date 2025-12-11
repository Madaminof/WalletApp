package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.CircularIconButton
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.getAccountColor
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@Composable
fun BalanceByAccountsCard(
    accounts: List<Account>,
    totalBalance: Double,
    formatter: DecimalFormat
) {
    val nonNegativeTotalBalance = totalBalance.coerceAtLeast(0.0)
    val cardBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    if (accounts.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(all = 16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Hisoblar Bo'yicha Balans",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = { /* Hisob filteri/tartiblash */ },
                    icon = Icons.Default.FilterList,
                    contentDescription = "Accounts filter",
                    tint = primaryAccent,
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }

            Spacer(Modifier.height(16.dp))

            PremiumStackedAccountBar(
                accounts = accounts,
                totalBalance = nonNegativeTotalBalance,
                formatter = formatter,
                primaryColor = primaryAccent,
                isDarkBackground = true
            )
        }
    }
}

@Composable
fun PremiumStackedAccountBar(
    accounts: List<Account>,
    totalBalance: Double,
    formatter: DecimalFormat,
    primaryColor: Color,
    isDarkBackground: Boolean = false
) {

    val activeAccounts = remember(accounts) { accounts.filter { it.initialBalance.absoluteValue > 0.0 } }

    val safeTotalBalance = if (totalBalance <= 0) 0.0001 else totalBalance // Nolga bo'lishni oldini olish

    val accountData = remember(activeAccounts, safeTotalBalance) {
        activeAccounts
            .map { account ->
                Triple(
                    getAccountColor(account, primaryColor),
                    (account.initialBalance / safeTotalBalance).toFloat().coerceIn(0f, 1f),
                    account
                )
            }
            .sortedByDescending { it.second }
    }

    val textColor = if (isDarkBackground) MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface
    val mutedColor = if (isDarkBackground) MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
    val barTrackColor = if (isDarkBackground) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
    val indicatorBorderColor = if (isDarkBackground) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.surface

    if (activeAccounts.isEmpty()) {
        Box(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
            Text("Hisoblar balanslari yo'q.", color = mutedColor, fontSize = 14.sp)
        }
        return
    }
    val totalProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, delayMillis = 100, easing = LinearOutSlowInEasing),
        label = "stacked_bar_anim"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp) // Kichikroq va ixchamroq
                .clip(RoundedCornerShape(5.dp))
                .background(barTrackColor)
        ) {
            accountData.forEach { (color, percentage, _) ->
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight((percentage * totalProgress).coerceAtLeast(0.001f), fill = true)
                        .background(color)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        accountData.forEach { (color, percentage, account) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rangli Nuqta
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(1.dp, indicatorBorderColor, CircleShape)
                )

                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        account.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        maxLines = 1,
                        modifier = Modifier.width(0.dp).weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "(${String.format("%.1f", percentage * 100)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = mutedColor,
                    )
                }
                Text(
                    "${formatter.format(account.initialBalance)} UZS",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = textColor,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}