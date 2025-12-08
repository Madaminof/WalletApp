package com.example.walletapp.wallet.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.presentation.ui.drawableMenu.DrawerBody
import com.example.walletapp.wallet.presentation.ui.drawableMenu.DrawerHeader
import com.example.walletapp.wallet.presentation.ui.home.HomeScreen
import com.example.walletapp.wallet.presentation.ui.home.HomeTopBar
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2.AddTransactionBottomSheet
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.TotalBalanceCardViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavController,
    addTransactionViewModel: AddTransactionViewModel = hiltViewModel(),
    totalBalanceCardViewModel: TotalBalanceCardViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    val navigateFromDrawer: (String) -> Unit = { route ->
        navController.navigate(route) {
            if (route == Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            launchSingleTop = true
        }
    }

    val navigateFromHomeAction: (String) -> Unit = { route ->
        navController.navigate(route)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

                DrawerHeader()
                DrawerBody(
                    navController = navController,
                    onDrawerClose = { scope.launch { drawerState.close() } },
                    onNavigate = navigateFromDrawer
                )
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onMenuClick = openDrawer
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isSheetOpen = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(50)
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Transaction"
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                HomeScreen(
                    onActionClick = navigateFromHomeAction,
                    navController = navController,
                    totalBalanceCardViewModel = totalBalanceCardViewModel
                    )

                if (isSheetOpen) {
                    AddTransactionBottomSheet(
                        viewModel = addTransactionViewModel,
                        onClose = { isSheetOpen = false },
                    )
                }
            }
        }
    }
}