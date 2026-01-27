package dev.samandar.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import dev.samandar.walletapp.wallet.presentation.ui.Navigation
import dev.samandar.walletapp.wallet.presentation.ui.home.SplashScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen.BudgetsScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.AddBudgetScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail.BudgetDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.homeAndBudgetGraph(
    navController: NavHostController,
    budgetViewModel: BudgetViewModel
) {
    val modalRoutes = listOf(
        Screen.Add.route,
        Screen.budjetAdd.route,
        Screen.addTransaction.route,
        Screen.addAccound.route
    )

    composable(Screen.Splash.route) {
        SplashScreen(navController = navController)
    }

    composable(
        Screen.Home.route,
        enterTransition = { ZoomInForward },
        popExitTransition = { ZoomOutBackward },
        exitTransition = {
            if (targetState.destination.route in modalRoutes) ModalExitTransition else ZoomOutForward
        },
        popEnterTransition = {
            if (initialState.destination.route in modalRoutes) {
                fadeIn(animationSpec = tween(MODAL_TRANSITION_DURATION))
            } else {
                ZoomInBackward
            }
        }
    ) {
        Navigation(navController = navController)
    }

    // 3. Budgets List Screen (Zoom animatsiyalari bilan)
    composable(
        route = Screen.Budgets.route,
        enterTransition = { ZoomInForward },
        exitTransition = { ZoomOutForward },
        popEnterTransition = { ZoomInBackward },
        popExitTransition = { ZoomOutBackward }
    ) {
        BudgetsScreen(
            viewModel = budgetViewModel,
            navController = navController
        )
    }

    // 4. Add Budget Screen (Modal)
    composable(
        route = Screen.budjetAdd.route,
        enterTransition = { ModalEnterTransition },
        exitTransition = { ModalExitTransition }
    ) {
        AddBudgetScreen(navController = navController)
    }

    // 5. Budget Detail Screen (To'liq original mantiq)
    composable(
        route = Screen.BudgetDetail.route,
        arguments = listOf(navArgument("budgetId") { type = NavType.StringType }),
        enterTransition = { ZoomInForward },
        exitTransition = { ZoomOutForward },
        popEnterTransition = { ZoomInBackward },
        popExitTransition = { ZoomOutBackward }
    ) { backStackEntry ->
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""

        // Kodingdagi collectAsState(initial = null) mantiqi
        val budgetStatus by budgetViewModel.getBudgetStatusById(budgetId)
            .collectAsState(initial = null)

        budgetStatus?.let { status ->
            BudgetDetailScreen(
                budgetStatus = status,
                onBack = { navController.popBackStack() },
                onDelete = {
                    budgetViewModel.deleteBudjet(status.budget)
                    navController.popBackStack()
                },
                viewModel = budgetViewModel,
            )
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}