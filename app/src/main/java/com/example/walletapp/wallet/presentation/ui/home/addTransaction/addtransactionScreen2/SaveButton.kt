package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SaveButton(enabled: Boolean, onClick: () -> Unit, primaryColor: Color) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // Kattaroq
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp), // Katta yumaloq burchaklar
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text(
            text = if (enabled) "SAQLASH" else "SAQLANMOQDA...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}