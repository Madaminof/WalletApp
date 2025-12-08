package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Constants
private const val DAY_OFFSET_MILLIS = 24 * 60 * 60 * 1000L

@Composable
fun DateTimeRow(viewModel: AddTransactionViewModel) {

    val transactionDateMillis = viewModel.uiState.selectedDate

    val currentLocale = Locale.getDefault()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", currentLocale) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", currentLocale) }

    val dateString = dateFormat.format(transactionDateMillis)
    val timeString = timeFormat.format(transactionDateMillis)

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }.apply { timeInMillis = transactionDateMillis }

    val accentColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.onPrimaryContainer
    val arrowColor = MaterialTheme.colorScheme.onTertiary .copy(0.7f)


    val dialogTheme = androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        dialogTheme,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            viewModel.onDateChange(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        dialogTheme,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            viewModel.onDateChange(calendar.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { viewModel.onDateChange(transactionDateMillis - DAY_OFFSET_MILLIS) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Filled.ArrowBackIosNew,
                contentDescription = "Bir kun orqaga surish",
                tint = arrowColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { datePickerDialog.show() }
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Sanani tanlash",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    dateString,
                    fontSize = 12.sp,
                    color =MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { timePickerDialog.show() }
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "Vaqtni tanlash",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    timeString,
                    fontSize = 12.sp,
                    color =MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        IconButton(
            onClick = { viewModel.onDateChange(transactionDateMillis + DAY_OFFSET_MILLIS) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = "Bir kun oldinga surish",
                tint = arrowColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}