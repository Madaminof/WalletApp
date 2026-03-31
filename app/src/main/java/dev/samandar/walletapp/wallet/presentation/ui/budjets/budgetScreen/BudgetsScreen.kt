package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.samandar.walletapp.R
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
    navController: NavController,
) {
    val budgetStatuses by viewModel.activeBudgetStatuses.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        val handle = navBackStackEntry?.savedStateHandle
        val message = handle?.get<String>("success_key")

        if (message != null) {
            var mediaPlayer: MediaPlayer? = null
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.add_transaction_sound)
                mediaPlayer?.setVolume(0.5f, 0.5f)
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
                    contentPadding = PaddingValues(top = 4.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(
                        items = budgetStatuses,
                        key = { _, item -> item.budget.id }
                    ) { index, status ->
                        BudgetRowItem(
                            status = status,
                            index = index,
                            onClick = { navController.navigate("budget_detail/${status.budget.id}") },
                            onDelete = { viewModel.deleteBudjet(status.budget) },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(500),
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                fadeOutSpec = tween(400)
                            ),
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
