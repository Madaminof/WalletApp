package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.accountDetail.AccountDetailBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.SelectAccountTypeBottomSheet
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

    var showTypeSelectionSheet by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.title_account),
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(
                        onClick = {
                            if (accounts.size < MAX_ACCOUNTS) {
                                showTypeSelectionSheet = true
                            } else {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(limitReachedMessage)
                                }
                            }
                        },
                        modifier = Modifier.offset(x = (-8).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Account",
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    }
                    if (showTypeSelectionSheet) {
                        SelectAccountTypeBottomSheet(
                            onTypeSelected = { type ->
                                showTypeSelectionSheet = false
                                navController.navigate("${Screen.addAccound.route}/${type.name}")
                            },
                            onDismiss = { showTypeSelectionSheet = false }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        if (accounts.isEmpty()) {
            EmptyAccountsState {
                navController.navigate(Screen.addAccound.route)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                itemsIndexed(
                    items = accounts,
                    key = { _, account -> account.id }
                ) { index, account ->
                    AnimatedAppearance(index = index) {
                        WalletCardItem(
                            account = account,
                            isDefault = account.isDefault,
                            onClick = { selectedAccount = it }
                        )
                    }
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
                },
                navController = navController
            )
        }
    }
}

@Composable
fun EmptyAccountsState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hozircha hisoblar mavjud emas",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Text(stringResource(R.string.add_account_title))
        }
    }
}

@Composable
fun AnimatedAppearance(index: Int, content: @Composable () -> Unit) {
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    val delay = index * 60

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(500, delayMillis = delay)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(500, delayMillis = delay)
                ),
        exit = fadeOut()
    ) {
        content()
    }
}