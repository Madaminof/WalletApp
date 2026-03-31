package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.viewmodel.CurrencyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionBottomSheet(
    onDismiss: () -> Unit,
    apiViewModel: CurrencyViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentMainCurrency by CurrencyManager.currentCurrency
    val bankRates by apiViewModel.rates.collectAsState()
    val isSyncing by apiViewModel.isSyncing.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            CurrencyHeader(
                isSyncing = isSyncing,
                onRefresh = { apiViewModel.refreshRates() },
                onClose = onDismiss
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp)
            ) {
                items(
                    items = CurrencyManager.supportedCurrencies,
                    key = { it }
                ) { currencyCode ->
                    val isSelected = currentMainCurrency == currencyCode

                    CurrencyItem(
                        code = currencyCode,
                        rateText = getRateSubtitle(currencyCode, currentMainCurrency, bankRates),
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                // Faqat yangi tanlangan valyuta kodini yuboramiz
                                apiViewModel.changeCurrency(currencyCode)

                                scope.launch {
                                    delay(200)
                                    onDismiss()
                                }
                            } else {
                                onDismiss()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyHeader(
    isSyncing: Boolean,
    onRefresh: () -> Unit,
    onClose: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSyncing) 360f else 0f,
        label = "syncRotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Asosiy valyuta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
            Text(
                text = "Barcha hisoblar ushbu valyutaga o'giriladi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
            )
        }

        Row {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }
        }
    }
}

@Composable
private fun CurrencyItem(
    code: String,
    rateText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(0.8f)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CurrencyFlagBox(code = code, isSelected = isSelected)

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onTertiary.copy(0.8f)

                    )
                    Text(
                        text = rateText,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(0.6f)
                        else MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                    )
                }

                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun CurrencyFlagBox(code: String, isSelected: Boolean) {
    val flagEmoji = when (code) {
        "UZS" -> "🇺🇿"
        "USD" -> "🇺🇸"
        "EUR" -> "🇪🇺"
        "RUB" -> "🇷🇺"
        else -> "🏳️"
    }
    val glowColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
                )
                .border(
                    width = if (isSelected) 0.dp else 1.dp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.05f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = flagEmoji,
                fontSize = 26.sp,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}


 fun getRateSubtitle(
    itemCode: String,
    currentMainCurrency: String,
    bankRates: List<dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity>
): String {
    // 1. Agar item joriy tanlangan valyuta bo'lsa
    if (itemCode == currentMainCurrency) return "Asosiy o'lchov birligi"

    // 2. Kurslarni topamiz
    val itemRateInUzs = bankRates.find { it.code == itemCode }?.rate
    val selectedRateInUzs = bankRates.find { it.code == currentMainCurrency }?.rate

    return when {
        // Asosiy valyuta UZS bo'lganda (Masalan: 1 USD = 12,800 UZS)
        currentMainCurrency == "UZS" && itemRateInUzs != null -> {
            "1 $itemCode = ${String.format("%,.0f", itemRateInUzs)} UZS"
        }

        // Item UZS bo'lib, asosiy valyuta boshqa bo'lsa (Masalan: 1 UZS = 0.000078 USD)
        itemCode == "UZS" && selectedRateInUzs != null -> {
            val oneUzsInSelected = 1.0 / selectedRateInUzs
            "1 UZS ≈ ${String.format("%.6f", oneUzsInSelected)} $currentMainCurrency"
        }

        // Cross-rate: Ikkala valyuta ham UZS bo'lmaganda (Masalan: 1 EUR = 1.10 USD)
        itemRateInUzs != null && selectedRateInUzs != null -> {
            val rate = itemRateInUzs / selectedRateInUzs
            // Professional aniqlik: Cross-rate uchun 4 ta raqam yetarli
            "1 $itemCode ≈ ${String.format("%.4f", rate)} $currentMainCurrency"
        }

        else -> "Kurs ma'lumotlari mavjud emas"
    }
}