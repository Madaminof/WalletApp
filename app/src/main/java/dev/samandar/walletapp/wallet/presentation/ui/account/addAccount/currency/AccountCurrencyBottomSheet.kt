package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.* // getValue uchun bu juda muhim
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyFlagBox
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.getRateSubtitle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCurrencyBottomSheet(
    onDismiss: () -> Unit,
    viewModel: AddAccountCurrencyViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔥 ViewModel'dan kelayotgan professional State'lar
    val uiState by viewModel.uiState.collectAsState()
    val currentCurrency by AddAccountCurrencyManager.localCurrency
    val bankRates by viewModel.rates.collectAsState()

    // 🚀 Xatoliklarni (null yoki internet xatolarini) Snackbar-da ko'rsatish
    LaunchedEffect(key1 = viewModel.errorEvent) {
        viewModel.errorEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(0.4f)) }
    ) {
        // Box orqali Snackbar-ni Column ustiga overlay qilamiz (Floating effekt)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Header (Optimallashgan)
                AccountCurrencyHeader(
                    isSyncing = uiState.isSyncing,
                    isOffline = uiState.isOffline,
                    onRefresh = { viewModel.refreshRates() }
                )

                // Kurslar ro'yxati
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = AddAccountCurrencyManager.supportedCurrencies,
                        key = { it }, // Recomposition-ni kamaytiradi
                        contentType = { "currency_item" } // Cache-lashni yaxshilaydi
                    ) { code ->
                        val isSelected = currentCurrency == code

                        CurrencyItem(
                            code = code,
                            rateText = getRateSubtitle(code, currentCurrency, bankRates),
                            isSelected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    viewModel.changeLocalCurrency(code)
                                    scope.launch {
                                        sheetState.hide()
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

            // 🛠 Professional SnackbarHost - Ro'yxat ustida chiroyli chiqishi uchun
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
 fun AccountCurrencyHeader(
    isSyncing: Boolean,
    isOffline: Boolean,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Strings.select_currency),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.9f)
                )
            )
            // Holat haqida tushunarli matn
            Text(
                text = when {
                    isSyncing -> stringResource(Strings.currency_status_syncing)
                    isOffline -> stringResource(Strings.currency_status_offline)
                    else -> stringResource(Strings.currency_status_default)
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (isOffline) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onTertiary.copy(0.5f)
            )
        }

        // 🔄 Professional aylanish animatsiyasi
        val rotation by animateFloatAsState(
            targetValue = if (isSyncing) 360f else 0f,
            animationSpec = if (isSyncing) {
                infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            } else {
                tween(500)
            },
            label = "syncIconRotation"
        )

        IconButton(
            onClick = onRefresh,
            enabled = !isSyncing, // Qayta-qayta bosishni cheklaymiz
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                CircleShape
            )
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Refresh",
                tint = if (isOffline) Color.Gray else MaterialTheme.colorScheme.primary,
                modifier = Modifier.rotate(rotation)
            )
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