package dev.samandar.walletapp.wallet.smartScann.scannScreen

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import dev.samandar.walletapp.wallet.smartScann.ReceiptAnalyzer
import dev.samandar.walletapp.wallet.smartScann.ScannerViewModel
import java.util.concurrent.Executors

@Composable
fun ReceiptScannerScreen(
    viewModel: ScannerViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(uiState.isScanning) {
        if (uiState.isScanning) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ReceiptAnalyzer(
                        onTextDetected = { text ->
                        },
                        onQrDetected = { qr ->
                            viewModel.onQrDetected(qr)
                        }
                    ))
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("Scanner", "Kamera xatosi", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        ScannerOverlayLayout(isProcessing = uiState.isProcessing)

        if (uiState.isProcessing) {
            ProcessingOverlay()
        }

        TopScannerBar(onClose = onClose)
    }
}

