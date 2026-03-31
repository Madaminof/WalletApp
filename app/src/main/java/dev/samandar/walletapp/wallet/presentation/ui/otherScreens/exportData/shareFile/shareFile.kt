package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.shareFile

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun shareFile(context: Context, file: File) {
    try {
        val authority = "${context.packageName}.provider"
        val uri = FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Hisobotni ulashish"))
    } catch (e: Exception) {
        Toast.makeText(context, "Faylni ulashishda xatolik: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}