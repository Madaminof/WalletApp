package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData


@Composable
fun CategoryStatItem(
    category: CategoryData,
    total: Double,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val percent = if (total > 0) (category.amount / total * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = category.color.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val safeIconId = remember(category.iconResId) {
                    val id = category.iconResId ?: 0
                    if (id > 0) {
                        getSafeIconId(context, id)
                    } else {
                        R.drawable.card_default_icon // Default ikonka
                    }
                }
                Icon(
                    painter = painterResource(id = safeIconId),
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.categoryName,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.4).sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "$percent%",
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = formatAmountWithCurrency(category.amount),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                thickness = 0.4.dp,
                color = Color.White.copy(alpha = 0.08f)
            )
        }
    }
}