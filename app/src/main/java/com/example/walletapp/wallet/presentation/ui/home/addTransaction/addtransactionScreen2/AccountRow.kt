package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.FormatAmount
import com.example.walletapp.wallet.presentation.utils.getCurrencySymbol
import java.text.DecimalFormat
val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun AccountRow(
    accounts: List<Account>,
    selected: Account?,
    onSelect: (Account) -> Unit
) {
    if (accounts.isEmpty()) return

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
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
    val premiumSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val colorTween = tween<Color>(durationMillis = 350)

    val defaultColor = MaterialTheme.colorScheme.primaryContainer.copy(0.7f)

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

    val targetContainerColor = if (isSelected) accColor else defaultColor
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = colorTween,
        label = "containerColorAnim"
    )
    val contentColorForSelected = if (accColor.luminance() > 0.5f) Color.Black else Color.White
    val contentColorForUnselected = MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    val targetIconTint = if (isSelected) contentColorForSelected else accColor
    val iconTint by animateColorAsState(
        targetValue = targetIconTint,
        animationSpec = colorTween,
        label = "iconTintAnim"
    )
    val targetTextColor = if (isSelected) contentColorForSelected else contentColorForUnselected
    val nameColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = colorTween,
        label = "nameColorAnim"
    )
    val balanceColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = colorTween,
        label = "balanceColorAnim"
    )
    val currencyColor by animateColorAsState(
        targetValue = if (isSelected) contentColorForSelected.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onTertiary.copy(0.8f),
        animationSpec = colorTween,
        label = "currencyColorAnim"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = premiumSpring,
        label = "scaleAnim"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),

        modifier = Modifier
            .scale(scale)
            .defaultMinSize(minWidth = 120.dp)
            .clickable { onSelect(acc) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = acc.iconResId ?: R.drawable.ic_card_default),
                contentDescription = acc.name,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    acc.name,
                    color = nameColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    style = LocalTextStyle.current.copy(lineHeight = 1.05.em)
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 0.dp)
                ) {
                    Text(
                        FormatAmount(acc.initialBalance),
                        color = balanceColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        maxLines = 1,
                        style = LocalTextStyle.current.copy(lineHeight = 1.05.em)
                    )
                }
            }
        }
    }
}