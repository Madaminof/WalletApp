package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.addDebt

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import dev.samandar.walletapp.wallet.domain.model.Account
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.AccountSelectionField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime.PremiumDateTimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddPaymentDialog(
    debt: Debt,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onConfirm: (Double, Account, Long, String?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var selectedAccount by remember {
        mutableStateOf(accounts.find { it.id == debt.accountId } ?: accounts.firstOrNull())
    }

    val activeColor = if (debt.type == DebtType.LENT) incomeColor else expenseColor

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .padding(24.dp)
            .widthIn(max = 400.dp),
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    selectedAccount?.let { onConfirm(amount, it, selectedDate, note) }
                },
                enabled = amountText.isNotEmpty() && selectedAccount != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.05f),
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        title = {
            Text(
                text = "${debt.personName} — ${stringResource(R.string.debt_payment)}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BasicTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            textAlign = TextAlign.Center,
                            color = activeColor,
                            fontWeight = FontWeight.Black
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        cursorBrush = SolidColor(activeColor),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (amountText.isEmpty()) {
                                    Text(
                                        "0",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.total_amount_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline.copy(0.6f)
                    )
                }

                AccountSelectionField(
                    accounts = accounts,
                    selectedAccount = selectedAccount,
                    onAccountSelect = { selectedAccount = it },
                    accentColor = activeColor
                )

                Surface(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = selectedDate.toFormattedDate(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )

    if (showDatePicker) {
        PremiumDateTimePickerDialog(
            initialDateTime = selectedDate,
            onConfirm = {
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}


fun Long.toFormattedDate(): String {
    val date = Date(this)
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.time = date
    val dateYear = calendar.get(Calendar.YEAR)

    return if (currentYear == dateYear) {
        val format = SimpleDateFormat("d-MMM", Locale.getDefault())
        format.format(date)
    } else {
        val format = SimpleDateFormat("d-MMM, yyyy", Locale.getDefault())
        format.format(date)
    }
}