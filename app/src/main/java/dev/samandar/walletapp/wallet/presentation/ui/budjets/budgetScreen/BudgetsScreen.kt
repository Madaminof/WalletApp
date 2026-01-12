package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetsScreen(
    viewModel: BudgetViewModel = hiltViewModel(),
    navController: NavController
) {
    val budgetStatuses by viewModel.activeBudgetStatuses.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        val handle = navBackStackEntry?.savedStateHandle
        val message = handle?.get<String>("success_key")

        if (message != null) {
            var mediaPlayer: MediaPlayer? = null
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.transaction_save)
                mediaPlayer?.setVolume(0.3f, 0.3f)
                mediaPlayer?.start()

                val snackJob = launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }

                delay(1000L)

                snackJob.cancel()
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                }
                snackbarHostState.currentSnackbarData?.dismiss()

                handle?.set("success_key", null)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaPlayer?.release()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = stringResource(Strings.title_budgets),
                    onBackClick = { navController.popBackStack() },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.budjetAdd.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Budget")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            if (budgetStatuses.isEmpty()) {
                EmptyBudgetsState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(
                        items = budgetStatuses,
                        key = { _, item -> item.budget.id }
                    ) { index, status ->
                        BudgetRowItem(
                            status = status,
                            onClick = {
                                navController.navigate("budget_detail/${status.budget.id}")
                            },
                            modifier = Modifier.animateItem(),
                            index = index,
                            onDelete = {
                                viewModel.deleteBudjet(status.budget)
                            }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),

            contentAlignment = Alignment.TopCenter
        ) {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AnimatedContent(
                    targetState = data,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { -it }) + fadeIn())
                            .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                    },
                    label = "ModernSnackbarAnim"
                ) { targetData ->
                    ModernSnackbar(targetData)
                }
            }
        }
    }
}
