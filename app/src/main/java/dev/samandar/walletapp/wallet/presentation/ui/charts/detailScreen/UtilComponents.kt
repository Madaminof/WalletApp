package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun IconBox(iconRes: Int?, color: Color) {
    val context = LocalContext.current

    // 1. Kelgan ID ni xavfsiz holatga keltiramiz
    val safeIconId = remember(iconRes) {
        val id = iconRes ?: 0
        if (id > 0) {
            getSafeIconId(context, id)
        } else {
            R.drawable.default_category // Agar bazada ID bo'lmasa default
        }
    }
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(0.1f)
    ) {
        Icon(
            painter = painterResource(id = safeIconId),
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(10.dp)
        )
    }
}



fun shareTransaction(
    context: Context,
    transaction: Transaction,
    amountText: String
) {
    val categoryName = transaction.category?.name ?: ""
    val accountName = transaction.account?.name ?: ""
    val note = transaction.note ?: ""

    val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(transaction.date)

    val shareText = """
📊 ${context.getString(R.string.details)}

${context.getString(R.string.category_label_title)}: $categoryName
${context.getString(R.string.detail_account)}: $accountName
${context.getString(R.string.transaction_amount)}: $amountText
${context.getString(R.string.date_time)}: $date
${if (note.isNotBlank()) "${context.getString(R.string.note_label)}: $note" else ""}

Wallet Analyst
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Log.e("ShareError", "Qabul qiluvchi ilova topilmadi: ${e.message}")
    } catch (e: Exception) {
        Log.e("ShareError", "Kutilmagan xato: ${e.message}")
    }
}
