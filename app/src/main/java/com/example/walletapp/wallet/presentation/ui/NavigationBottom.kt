
package com.example.walletapp.wallet.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.auth.presentation.AuthViewModel
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.presentation.ui.account.WalletScreen
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetsScreen
import com.example.walletapp.wallet.presentation.ui.charts.ChartsScreen
import com.example.walletapp.wallet.presentation.ui.drawableMenu.DrawerBody
import com.example.walletapp.wallet.presentation.ui.drawableMenu.DrawerHeader
import com.example.walletapp.wallet.presentation.ui.home.HomeBottomBar
import com.example.walletapp.wallet.presentation.ui.home.HomeScreen
import com.example.walletapp.wallet.presentation.ui.home.HomeTopBar
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    user: FirebaseUser?,
    navController: NavController,
    viewModel: HomeViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    scrollState: ScrollState = rememberScrollState(),
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var previousIndex by remember { mutableStateOf(0) }
    val navigateToScreen: (Int) -> Unit = { index ->
        previousIndex = selectedIndex
        selectedIndex = index
    }
    val accounts by viewModel.accounts.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }


    val isScrolledDown = remember {
        derivedStateOf { scrollState.value > 100 }
    }
    val fabVisibility by animateFloatAsState(
        targetValue = if (isScrolledDown.value) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "FABVisibility"
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                DrawerHeader(user = user)
                DrawerBody(
                    navController = navController,
                    onDrawerClose = { scope.launch { drawerState.close() } },
                    onBottomBarScreenSelected = navigateToScreen,
                    authViewModel = authViewModel
                )
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    user = user,
                    selectedIndex = selectedIndex,
                    onMenuClick = openDrawer
                )
            },
            bottomBar = {
                HomeBottomBar(selectedIndex = selectedIndex) { index ->
                    previousIndex = selectedIndex
                    selectedIndex = index
                }
            },
            floatingActionButton = {
                if (selectedIndex == 0) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.addTransaction.route) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(50)
                    ){
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Transaction"
                        )
                    }
                }
                else if (selectedIndex == 2){
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.budjetAdd.route) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Budget"
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = selectedIndex,
                    transitionSpec = {
                        val isMovingForward = targetState > previousIndex
                        if (isMovingForward) {
                            slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    }
                ) { screen ->
                    when (screen) {
                        0 -> HomeScreen(onActionClick = navigateToScreen,navController)
                        1 -> ChartsScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                        2 -> BudgetsScreen(
                            viewModel = budgetViewModel,
                            navController = navController
                        )
                        3 -> WalletScreen(accounts = accounts, navController = navController, accountViewModel = accountViewModel)
                    }
                }
            }
        }
    }
}
