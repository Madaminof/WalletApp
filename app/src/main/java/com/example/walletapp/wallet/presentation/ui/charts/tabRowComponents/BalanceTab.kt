package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab.BalanceByAccountsCard
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab.BalanceTrendCard
import java.text.DecimalFormat


val primaryAccent = Color(0xFF4759C1)

fun getAccountColor(account: Account, primaryColor: Color): Color {
    val hexString = account.colorHex
    if (!hexString.isNullOrBlank()) {
        return try {
            Color(android.graphics.Color.parseColor(hexString))
        } catch (e: IllegalArgumentException) {
            primaryColor
        }
    }
    return primaryColor
}


@Composable
fun CircularIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    backgroundColor: Color = Color.Transparent,
    size: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

@Composable
fun EmptyChartView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FilterList,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Trend ma'lumotlari mavjud emas.",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun BalanceTab(
    viewModel: BalanceTabViewModel = hiltViewModel()
) {
    val state by viewModel.balanceState.collectAsStateWithLifecycle()

    if (state.error != null && !state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Ma'lumotlarni yuklashda xato: ${state.error}", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            BalanceTrendCard(viewModel)
        }
        item {
            BalanceByAccountsCard(state.accounts, state.totalBalance, remember { DecimalFormat("#,###.##") })
        }
    }
}