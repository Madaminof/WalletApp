package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background // Kerak bo'lmasligi mumkin, lekin qoldiramiz
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Account
import java.text.DecimalFormat

@Composable
fun AccountRow(
    accounts: List<Account>,
    selected: Account?,
    onSelect: (Account) -> Unit
) {
    if (accounts.isEmpty()) return

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(accounts, key = { it.id }) { acc ->
            AccountItem(
                acc = acc,
                isSelected = acc == selected,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun AccountItem(
    acc: Account,
    isSelected: Boolean,
    onSelect: (Account) -> Unit,
) {
    val defaultColor = MaterialTheme.colorScheme.onTertiary.copy(0.05f)
    val animationDuration = 300 // ms

    val accColor: Color = remember(acc.colorHex) {
        val hexString = acc.colorHex
        if (hexString.isNullOrBlank()) {
            return@remember defaultColor
        }

        try {
            Color(hexString.toColorInt())
        } catch (e: IllegalArgumentException) {
            defaultColor
        }
    }

    // Asosiy rang animatsiyalari
    val targetContainerColor = if (isSelected) accColor else defaultColor
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(animationDuration), label = "containerColorAnim"
    )

    // Kontent ranglari uchun
    val primaryContentColor = if (accColor.luminance() > 0.5f) Color.Black else Color.White

    // Icon Tint
    val targetIconTint = if (isSelected) primaryContentColor else accColor
    val iconTint by animateColorAsState(
        targetValue = targetIconTint,
        animationSpec = tween(animationDuration), label = "iconTintAnim"
    )

    // Account Name rangi
    val targetNameColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    val nameColor by animateColorAsState(
        targetValue = targetNameColor,
        animationSpec = tween(animationDuration), label = "nameColorAnim"
    )

    // Balance rangi
    val targetBalanceColor = if (isSelected) Color.White.copy(0.9f) else MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    val balanceColor by animateColorAsState(
        targetValue = targetBalanceColor,
        animationSpec = tween(animationDuration), label = "balanceColorAnim"
    )

    // Currency rangi
    val targetCurrencyColor = if (isSelected) Color.White.copy(0.7f) else MaterialTheme.colorScheme.onTertiary.copy(0.4f)
    val currencyColor by animateColorAsState(
        targetValue = targetCurrencyColor,
        animationSpec = tween(animationDuration), label = "currencyColorAnim"
    )

    // Kattalashish Animatsiyasi
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = tween(animationDuration), label = "scaleAnim"
    )

    val balanceFormatter = remember { DecimalFormat("#,##0") }
    val formattedBalance = balanceFormatter.format(acc.initialBalance).replace(",", " ")

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),

        modifier = Modifier
            .scale(scale)
            .defaultMinSize(minWidth = 100.dp)
            .clickable { onSelect(acc) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = acc.iconResId ?: R.drawable.ic_card_default),
                contentDescription = acc.name,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    acc.name,
                    color = nameColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 10.sp
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        formattedBalance,
                        color = balanceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "UZS",
                        color = currencyColor,
                        fontSize = 8.sp,
                        lineHeight = 7.sp,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )
                }
            }
        }
    }
}