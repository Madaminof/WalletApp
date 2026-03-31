package dev.samandar.walletapp.wallet.smartScannQR.scannScreen

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.smartScannQR.ReceiptAnalyzer
import dev.samandar.walletapp.wallet.smartScannQR.ScannerViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ReceiptScannerScreen(
    viewModel: ScannerViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Flash boshqaruvi uchun state
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    // Ekran yopilganda flashni o'chirish va executorni tozalash
    DisposableEffect(Unit) {
        onDispose {
            cameraControl?.enableTorch(false)
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Kamera Preview
        if (uiState.isScanning) {
            CameraPreviewContainer(
                viewModel = viewModel,
                cameraExecutor = cameraExecutor,
                onCameraControlReady = { cameraControl = it },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Processing Blur (Faqat tahlil paytida)
        AnimatedVisibility(
            visible = uiState.isProcessing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .blur(15.dp)
            )
        }

        // 3. Skaner Ramkasi va Lazer
        ScannerOverlayLayout(isProcessing = uiState.isProcessing)

        // 4. Ma'lumot tahlil qilinmoqda...
        if (uiState.isProcessing) {
            ProcessingOverlay()
        }

        // 5. Yuqori Panel (Flashlight shu yerda ishlaydi)
        TopScannerBar(
            onClose = onClose,
            onFlashToggle = { isEnabled ->
                cameraControl?.enableTorch(isEnabled)
            }
        )
/*
        // 6. Pastki Premium OCR qismi
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            PremiumOcrAction(
                viewModel = viewModel,
                isProcessing = uiState.isProcessing
            )
        }*/
    }
}

@Composable
fun CameraPreviewContainer(
    viewModel: ScannerViewModel,
    cameraExecutor: ExecutorService,
    onCameraControlReady: (CameraControl) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, ReceiptAnalyzer(
                    onTextDetected = { /* OCR handled by Image picker */ },
                    onQrDetected = { qr -> viewModel.onQrDetected(qr) }
                ))
            }

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
            // Flashni boshqarish pultini uzatamiz
            onCameraControlReady(camera.cameraControl)
        } catch (e: Exception) {
            Log.e("Scanner", "Camera Binding Error", e)
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@Composable
fun PremiumOcrAction(
    viewModel: ScannerViewModel,
    isProcessing: Boolean
) {
    AnimatedVisibility(
        visible = !isProcessing,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    dev.samandar.walletapp.wallet.smartScannOCR.addOCR.AddOcrSection(viewModel = viewModel)
                }
            }

            // Premium Diamond Label
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = (-10).dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.premium_ic),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.Black
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "PREMIUM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    ),
                    color = Color.Black
                )
            }
        }
    }
}