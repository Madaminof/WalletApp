package com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.wallet.presentation.utils.getCurrencySymbol
import java.util.Currency
import java.util.Locale

@Composable
fun CurrencySelectionDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentCurrency by CurrencyManager.currentCurrency

    var tempSelectedCurrency by remember { mutableStateOf(currentCurrency) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Currency",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CurrencyManager.supportedCurrencies.forEach { currencyCode ->
                        val isChecked = tempSelectedCurrency == currencyCode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { tempSelectedCurrency = currencyCode }
                                .background(if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currencyCode,
                                    fontSize = 18.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${getCurrencySymbol(currencyCode)})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiary.copy(0.4f),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Tanlangan",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = tempSelectedCurrency != currentCurrency,
                    onClick = {
                        CurrencyManager.saveCurrency(context, tempSelectedCurrency)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                        disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
                        )
                ) {
                    Text(
                        text = "Save ($tempSelectedCurrency)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}