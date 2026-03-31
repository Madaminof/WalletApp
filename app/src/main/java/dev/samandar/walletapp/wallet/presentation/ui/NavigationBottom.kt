package dev.samandar.walletapp.wallet.presentation.ui

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.presentation.ui.drawableMenu.DrawerBody
import dev.samandar.walletapp.wallet.presentation.ui.drawableMenu.DrawerHeader
import dev.samandar.walletapp.wallet.presentation.ui.home.HomeScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.homeTopBar.HomeTopBar
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.TotalBalanceCardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavController,
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        val handle = navBackStackEntry?.savedStateHandle
        val message = handle?.get<String>("success_key")

        if (!message.isNullOrBlank()) {
            handle.set("success_key", null)

            var mediaPlayer: MediaPlayer? = null
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.add_transaction_sound)
                mediaPlayer?.setVolume(0.5f, 0.5f)
                mediaPlayer?.start()

                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                scope.launch {
                    delay(3000L)
                    mediaPlayer?.release()
                }
            }
        }
    }

    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val onNavigate: (String) -> Unit = { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                drawerTonalElevation = 0.dp,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                DrawerBody(
                    currentRoute = currentRoute,
                    onDrawerClose = { scope.launch { drawerState.close() } },
                    onNavigate = onNavigate
                )
            }
        }
    ){
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    HomeTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        navController = navController
                    )
                },
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    HomeScreen(
                        onActionClick = { route -> navController.navigate(route) },
                        navController = navController,
                        listState = listState
                    )
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
}