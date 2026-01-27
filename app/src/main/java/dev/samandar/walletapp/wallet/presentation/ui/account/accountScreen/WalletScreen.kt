package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.AccountDetailBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import kotlinx.coroutines.launch

const val MAX_ACCOUNTS = 6

@Composable
fun WalletScreen(
    accounts: List<Account>,
    navController: NavController,
    accountViewModel: AccountViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    val limitReachedMessage = stringResource(R.string.add_account_limit_reached, MAX_ACCOUNTS)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.title_account),
                onBackClick = { navController.popBackStack() },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                        )

                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { data.dismiss() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "OK",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = accounts,
                key = { _, account -> account.id }
            ) { index, account ->
                AnimatedAppearance(index = index) {
                    WalletCardItem(
                        account = account,
                        isDefault = index < 2,
                        onClick = { selectedAccount = it }
                    )
                }
            }
            item {
                AnimatedAppearance(index = accounts.size) {
                    WalletCardAdd(
                        canAddMore = accounts.size < MAX_ACCOUNTS,
                        onClick = {
                            if (accounts.size < MAX_ACCOUNTS) {
                                navController.navigate(Screen.addAccound.route)
                            } else {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(limitReachedMessage)
                                }
                            }
                        }
                    )
                }
            }
        }

        selectedAccount?.let { account ->
            AccountDetailBottomSheet(
                account = account,
                onDismiss = { selectedAccount = null },
                onUpdate = { acc ->
                    selectedAccount = null
                    navController.navigate(Screen.editAccount.route + "/${acc.id}")
                },
                onDelete = { acc ->
                    accountViewModel.deleteAccount(acc)
                    selectedAccount = null
                }
            )
        }
    }
}

@Composable
fun AnimatedAppearance(index: Int, content: @Composable () -> Unit) {
    val visibleState = remember { MutableTransitionState(false) }.apply { targetState = true }
    val delay = index * 80

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(600, delayMillis = delay)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(600, delayMillis = delay)
                ),
        exit = fadeOut()
    ) {
        content()
    }
}