package com.example.walletapp.wallet.presentation.ui.otherScreens.debts


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun EmptyDebtsState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.HourglassEmpty,
            contentDescription = "Qarzlar mavjud emas",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text("Hozircha qarzlar mavjud emas.", color = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
    }
}