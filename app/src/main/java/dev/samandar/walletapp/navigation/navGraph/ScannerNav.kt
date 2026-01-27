package dev.samandar.walletapp.navigation

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.*
import dev.samandar.walletapp.wallet.smartScann.*
import dev.samandar.walletapp.wallet.smartScann.scannScreen.ReceiptScannerScreen
import dev.samandar.walletapp.wallet.smartScann.scanReviewScreen.ScanReviewScreen

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.scannerGraph(navController: NavHostController) {
    composable(Screen.SCANNER.route) {
        val context = LocalContext.current as ComponentActivity
        val scannerViewModel: ScannerViewModel = hiltViewModel()
        val reviewViewModel: ReviewViewModel = hiltViewModel(context)
        val uiState by scannerViewModel.uiState.collectAsState()
        val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

        LaunchedEffect(Unit) { if (!cameraPermissionState.status.isGranted) cameraPermissionState.launchPermissionRequest() }
        LaunchedEffect(uiState.scanResult) {
            uiState.scanResult?.let { receipt ->
                reviewViewModel.setScannedReceipt(receipt)
                navController.navigate(Screen.REVIEW.route) { popUpTo(Screen.SCANNER.route) { inclusive = true } }
            }
        }

        if (cameraPermissionState.status.isGranted) {
            ReceiptScannerScreen(viewModel = scannerViewModel, onClose = { navController.popBackStack() })
        } else {
            PermissionRationaleUI(onRequestPermission = { cameraPermissionState.launchPermissionRequest() })
        }
    }

    composable(Screen.REVIEW.route) {
        val context = LocalContext.current as ComponentActivity
        val reviewViewModel: ReviewViewModel = hiltViewModel(context)
        val state by reviewViewModel.uiState.collectAsState()
        state.receipt?.let {
            ScanReviewScreen(state = state, onConfirmed = { reviewViewModel.saveFinalReceipt { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } } }, viewModel = reviewViewModel)
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }
}