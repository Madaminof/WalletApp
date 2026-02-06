package dev.samandar.walletapp.wallet.smartScannOCR.addOCR

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.smartScannQR.ScannerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOcrSection(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showSourceDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val defaultItemName = stringResource(Strings.label_total_purchase)

    // 1. Galereya uchun launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(
                it,
                defaultItemName = defaultItemName
            )
        }
    }

    // 2. Kamera uchun launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let {
                viewModel.onImageSelected(
                    it,
                    defaultItemName = defaultItemName
                )
            }
        }
    }

    // Kamera URI yaratish funksiyasi
    fun createTempUri(): Uri {
        val tempFile = File.createTempFile("receipt_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    // Skanerlash tugmasi
    Button(
        onClick = { showSourceDialog = true },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.scanner_ic2),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(Strings.scan_receipt_ocr))
    }

    // Manba tanlash dialogi
    if (showSourceDialog) {
        ModalBottomSheet(
            onDismissRequest = { showSourceDialog = false },
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stringResource(Strings.select_source),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onTertiary
                )

                SourceItem(
                    icon = R.drawable.camera_ic,
                    label = stringResource(Strings.take_photo),
                    onClick = {
                        showSourceDialog = false
                        val uri = createTempUri()
                        tempImageUri = uri
                        cameraLauncher.launch(uri)
                    }
                )
                SourceItem(
                    icon = R.drawable.gallery_ic,
                    label = stringResource(Strings.choose_gallery),
                    onClick = {
                        showSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }
}

@Composable
fun SourceItem(
    icon: Int,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
    }
}