package dev.samandar.walletapp.wallet.smartScannQR.scanReviewScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.smartScannQR.ReviewUiState
import dev.samandar.walletapp.wallet.smartScannQR.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReviewScreen(
    state: ReviewUiState,
    viewModel: ReviewViewModel,
    onBack: () -> Unit,
    onConfirmed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val accounts by viewModel.accounts.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Strings.title_review_purchase),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.back_ic),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary.copy(0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        state.receipt?.let { receipt ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 140.dp)
                ) {
                    item {
                        ReceiptHeroSection(receipt)
                    }

                    item {
                        MainFinanceSelection(
                            state = uiState,
                            categories = categories,
                            accounts = accounts,
                            onAccountSelected = { viewModel.updateAccount(it) },
                            onCategorySelected = { viewModel.updateCategory(it) }
                        )
                    }
                    item { ReceiptDetailCard(receipt) }

                    item {
                        SectionHeader(
                            title = stringResource(Strings.label_purchase_content),
                            count = receipt.items.size
                        )
                    }

                    item {
                        ReceiptItemsCard(items = receipt.items)
                    }
                }

                BottomActionArea(
                    isSaving = state.isSaving,
                    enabled = uiState.selectedAccount != null && uiState.selectedCategory != null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    onConfirmed = onConfirmed
                )
            }
        }
    }
}

