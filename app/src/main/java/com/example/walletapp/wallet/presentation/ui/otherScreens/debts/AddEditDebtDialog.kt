import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Debt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


private fun formatAmountToInput(amount: Double): String {
    return String.format(Locale.US, "%.2f", amount).removeSuffix(".00").removeSuffix(".0")
}

@Composable
fun AddEditDebtDialog(
    initialDebt: Debt?,
    accounts: List<Account>,
    selectedAccount: Account?,
    selectedDate: Long,
    onAccountSelect: (Account) -> Unit,
    onDateSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (person: String, amount: Double, isLent: Boolean) -> Unit
) {
    val isEditing = initialDebt != null && initialDebt.id.isNotBlank()
    val dialogTitle = if (isEditing) "Qarzni Tahrirlash" else "Yangi Qarz Qo'shish"

    var personName by remember { mutableStateOf(initialDebt?.person ?: "") }
    var amountText by remember {
        val initialAmount = initialDebt?.amount
        mutableStateOf(if (initialAmount != null && initialAmount > 0.0) formatAmountToInput(initialAmount) else "")
    }
    var isLent by remember { mutableStateOf(initialDebt?.isLent ?: true) }

    val amount = amountText.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0

    val isConfirmEnabled = remember(personName, amount, selectedAccount) {
        personName.isNotBlank() && amount > 0 && selectedAccount != null
    }

    val lentColor = expenseColor
    val owedColor = incomeColor
    val primaryActionColor = if (isLent) lentColor else owedColor
    val dialogBgColor = MaterialTheme.colorScheme.onPrimaryContainer

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = dialogBgColor,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier
                            .width(40.dp)
                            .clip(CircleShape),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Text(
                    text = dialogTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryActionColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isLent) "Pul berilmoqda (Chiqim)" else "Pul olinmoqda (Kirim)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Divider(color = MaterialTheme.colorScheme.onTertiary.copy(0.2f), thickness = 1.dp)
                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    DebtTypeButton(
                        text = "Qarz berish",
                        selected = isLent,
                        onClick = { isLent = true },
                        modifier = Modifier.weight(1f),
                        selectedColor = lentColor
                    )
                    Spacer(Modifier.width(10.dp))
                    DebtTypeButton(
                        text = "Qarz olish",
                        selected = !isLent,
                        onClick = { isLent = false },
                        modifier = Modifier.weight(1f),
                        selectedColor = owedColor
                    )
                }

                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Shaxs nomi", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryActionColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                        focusedBorderColor = primaryActionColor.copy(0.3f),
                    )

                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { newText -> amountText = newText.filter { it.isDigit() || it == '.' } },
                    label = { Text("Summa (so'm)",color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Money, contentDescription = null, tint = primaryActionColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                        focusedBorderColor = primaryActionColor.copy(0.3f),
                    )

                )
                Spacer(Modifier.height(12.dp))

                AccountSelectionDropdown(
                    accounts = accounts,
                    selectedAccount = selectedAccount,
                    onAccountSelect = onAccountSelect,
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = primaryActionColor.copy(0.3f)
                )
                Spacer(Modifier.height(12.dp))

                DateSelectionField(
                    selectedDate = selectedDate,
                    onDateSelect = onDateSelect,
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = primaryActionColor
                )

                Spacer(Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
                    ) {
                        Text("Bekor qilish", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = { onConfirm(personName, amount, isLent) },
                        enabled = isConfirmEnabled,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryActionColor)
                    ) {
                        Text(if (isEditing) "Saqlash" else "Qo'shish",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelectionDropdown(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelect: (Account) -> Unit,
    modifier: Modifier,
    primaryColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedAccount?.name ?: "Hisobni tanlang",
            onValueChange = {},
            readOnly = true,
            label = { Text("Hisob", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor.copy(0.3f),
                focusedLabelColor = primaryColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),


            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name, color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
                    onClick = {
                        onAccountSelect(account)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionField(
    selectedDate: Long,
    onDateSelect: (Long) -> Unit,
    modifier: Modifier,
    primaryColor: Color
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val dateText = remember(selectedDate) { dateFormatter.format(Date(selectedDate)) }
    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
    ) {
        OutlinedTextField(
            value = dateText,
            onValueChange = { /* ReadOnly */ },
            label = { Text("Sana") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = primaryColor) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                focusedBorderColor = primaryColor.copy(0.3f),
                focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                disabledBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                disabledTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                focusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                disabledLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
            )
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate,
            initialDisplayMode = DisplayMode.Picker
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val startOfDay = getStartOfDay(it)
                        onDateSelect(startOfDay)
                    }
                    showDatePicker = false
                }) { Text("Tanlash") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Bekor qilish") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}


@Composable
fun DebtTypeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    selectedColor: Color
) {
    val containerColor = if (selected) selectedColor else MaterialTheme.colorScheme.onTertiary.copy(0.1f)
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary.copy(0.5f)

    val border = if (selected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        border = border,
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if(selected) 4.dp else 0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(text, fontSize = 15.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}