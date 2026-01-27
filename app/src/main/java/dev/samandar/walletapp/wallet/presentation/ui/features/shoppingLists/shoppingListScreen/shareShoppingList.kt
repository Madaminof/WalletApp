package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import java.text.NumberFormat
import java.util.Locale


fun shareShoppingList(
    context: Context,
    listName: String,
    totalAmount: Double,
    itemDetails: List<String>
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }
    val formattedTotalAmount = numberFormat.format(totalAmount)

    val cleanItemDetails = itemDetails
        .mapNotNull { it.trim() }
        .filter { it.isNotBlank() }

    val itemsFormatted = if (cleanItemDetails.isNotEmpty()) {
        cleanItemDetails.joinToString(separator = "\n")
    } else {
        // Matnni resursdan olish
        context.getString(R.string.share_list_no_items)
    }

    val shareText = """
${context.getString(R.string.share_list_title)}

${context.getString(R.string.share_list_name)}: $listName
${context.getString(R.string.share_list_item_count)}: (${cleanItemDetails.size})

$itemsFormatted

${context.getString(R.string.share_list_total_amount)}: $formattedTotalAmount $activeCurrency
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
        // Loglarni o'zgartirmaymiz, ular texnik ma'lumot
        Log.e("ShareError", "Qabul qiluvchi ilova topilmadi: ${e.message}")
    } catch (e: Exception) {
        Log.e("ShareError", "Intentni ishga tushirishda kutilmagan xato: ${e.message}")
    }
}

@Composable
fun ShareButton(onShare: () -> Unit) {
    OutlinedButton(
        onClick = onShare,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            Icons.Default.Share,
            contentDescription = stringResource(R.string.list_row_share_desc),
            modifier = Modifier.size(16.dp)
        )

        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.list_row_share_button),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}
