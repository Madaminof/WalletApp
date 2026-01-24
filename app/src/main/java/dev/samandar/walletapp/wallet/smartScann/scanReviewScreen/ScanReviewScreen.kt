package dev.samandar.walletapp.wallet.smartScann.scanReviewScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.smartScann.ReviewUiState
import dev.samandar.walletapp.wallet.smartScann.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReviewScreen(
    state: ReviewUiState,
    viewModel: ReviewViewModel,
    onConfirmed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val accounts by viewModel.accounts.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Strings.title_review_purchase), color = MaterialTheme.colorScheme.onTertiary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        state.receipt?.let { receipt ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // 1. Header (Summa)
                    item { ReceiptHeroSection(receipt) }

                    // 2. Moliyaviy Cardlar
                    item {
                        MainFinanceSelection(
                            state = uiState,
                            categories = categories,
                            accounts = accounts,
                            onAccountSelected = { selectedAccount ->
                                viewModel.updateAccount(selectedAccount)
                            },
                            onCategorySelected = { selectedCategory ->
                                viewModel.updateCategory(selectedCategory)
                            }
                        )
                    }

                    // 3. Mahsulotlar Sarlavhasi
                    item { SectionHeader(title = stringResource(Strings.label_purchase_content), count = receipt.items.size) }


                    // 4. Mahsulotlar ro'yxati
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            ),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                receipt.items.forEachIndexed { index, item ->
                                    MutedItemRow(
                                        item = item,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    )

                                    // Divider faqat o'rtadagilar uchun
                                    if (index < receipt.items.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                BottomActionArea(
                    isSaving = state.isSaving,
                    enabled = state.selectedAccount != null,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onConfirmed = onConfirmed
                )
            }
        }
    }
}

