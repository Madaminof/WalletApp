package com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.util.*
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent // primaryAccent ni import qilganingizni faraz qildim

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var note by remember { mutableStateOf(transaction.note ?: "") }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var selectedAccount by remember { mutableStateOf(transaction.account) }
    var selectedDate by remember { mutableStateOf(Date(transaction.date)) }

    val allCategories by homeViewModel.allCategories.collectAsState(initial = emptyList())
    val accounts by homeViewModel.accounts.collectAsState(initial = emptyList())

    val filteredCategories = remember(allCategories, transaction.category) {
        val type = transaction.category?.type
        if (type == null) allCategories else allCategories.filter { it.type == type }
    }

    val dialogBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    val isFormValid = selectedCategory != null && selectedAccount != null && amount.toDoubleOrNull()?.let { it > 0.0 } == true

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = dialogBackgroundColor,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // 1. Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier.width(40.dp).clip(CircleShape),
                        thickness = 4.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
                Text(
                    text = "Tranzaksiyani Tahrirlash",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summa
                    PremiumAmountInput(
                        amount = amount,
                        onAmountChange = { amount = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = dialogBackgroundColor,
                            unfocusedContainerColor = dialogBackgroundColor,
                            focusedBorderColor = primaryAccent,
                            unfocusedBorderColor = textColor.copy(alpha = 0.5f),
                            cursorColor = primaryAccent
                        ),
                        textColor = textColor
                    )
                    PremiumDropdownSelector(
                        label = "Kategoriya",
                        icon = Icons.Default.Category,
                        selectedValue = selectedCategory?.name ?: "Tanlanmagan",
                        items = filteredCategories,
                        onItemSelected = { selectedCategory = it },
                        itemContent = { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                // Icon
                                Icon(
                                    painter = painterResource(id = cat.iconResId?: R.drawable.ic_add),
                                    contentDescription = null,
                                    tint = Color(cat.colorArgb.toInt()),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(cat.name, fontWeight = FontWeight.Medium, color = textColor)
                            }
                        }
                    )
                    PremiumDropdownSelector(
                        label = "Hamyon",
                        icon = Icons.Default.Wallet,
                        selectedValue = selectedAccount?.name ?: "Tanlanmagan",
                        items = accounts,
                        onItemSelected = { selectedAccount = it },
                        itemContent = { acc ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                val accountColor = acc.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: primaryAccent
                                Icon(
                                    painter = painterResource(id = acc.iconResId?:R.drawable.ic_add),
                                    contentDescription = null,
                                    tint = accountColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(acc.name, fontWeight = FontWeight.Medium, color = textColor)
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumDateSelector(selectedDate = selectedDate, onDateSelected = { newDate ->
                                val calOld = Calendar.getInstance().apply { time = selectedDate }
                                val calNew = Calendar.getInstance().apply { time = newDate }
                                calNew.set(Calendar.HOUR_OF_DAY, calOld.get(Calendar.HOUR_OF_DAY))
                                calNew.set(Calendar.MINUTE, calOld.get(Calendar.MINUTE))
                                selectedDate = calNew.time
                            }, containerColor = dialogBackgroundColor, primaryColor = primaryAccent, textColor = textColor)
                        }
                        Box(modifier = Modifier.width(100.dp)) {
                            PremiumTimeSelector(selectedDate = selectedDate, onTimeSelected = { selectedDate = it }, containerColor = dialogBackgroundColor, primaryColor = primaryAccent, textColor = textColor)
                        }
                    }
                    PremiumNoteInput(
                        note = note,
                        onNoteChange = { note = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = dialogBackgroundColor,
                            unfocusedContainerColor = dialogBackgroundColor,
                            focusedBorderColor = primaryAccent,
                            unfocusedBorderColor = textColor.copy(alpha = 0.5f),
                            cursorColor = primaryAccent
                        ),
                        textColor = textColor
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bekor qilish
                    TextButton(onClick = onClose) {
                        Text(
                            "Bekor qilish",
                            fontWeight = FontWeight.SemiBold,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newAmount = amount.toDoubleOrNull() ?: return@Button
                            val updated = transaction.copy(
                                amount = newAmount,
                                category = selectedCategory,
                                account = selectedAccount,
                                note = note,
                                date = selectedDate.time
                            )
                            scope.launch {
                                homeViewModel.updateTransaction(updated)
                                onClose()
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                    ) {
                        Text("Saqlash", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumAmountInput(amount: String, onAmountChange: (String) -> Unit, colors: TextFieldColors, textColor: Color) {
    OutlinedTextField(
        value = amount,
        onValueChange = { onAmountChange(it.replace(',', '.')) },
        label = { Text("Summa", color = textColor.copy(alpha = 0.5f)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Text("UZS", fontWeight = FontWeight.SemiBold, color = primaryAccent) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        textStyle = LocalTextStyle.current.copy(color = textColor)
    )
}

@Composable
fun <T> PremiumDropdownSelector(
    label: String,
    icon: ImageVector,
    selectedValue: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
) {
    var expanded by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    var parentWidth by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    parentWidth = coordinates.size.width
                }
        ) {
            Surface(
                modifier = Modifier
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clickable {
                        expanded = !expanded
                        rotation = if (expanded) 180f else 0f
                    },
                shape = RoundedCornerShape(12.dp),
                color = containerColor // Dinamik fon
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = primaryColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(selectedValue, fontWeight = FontWeight.Medium, color = textColor)
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer(rotationZ = rotation),
                        tint = textColor
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { parentWidth.toDp() })
                    .heightIn(max = 300.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(containerColor)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { itemContent(item) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumDateSelector(selectedDate: Date, onDateSelected: (Date) -> Unit, containerColor: Color, primaryColor: Color, textColor: Color) {
    var showDatePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { showDatePicker = true },
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = primaryColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(android.text.format.DateFormat.format("dd MMM yyyy", selectedDate).toString(), fontWeight = FontWeight.Medium, fontSize = 13.sp, color = textColor)
        }
    }

    if (showDatePicker) {
        // DatePicker dialogini stilini yaxshilash kerak bo'lsa, uning o'rniga Custom Dialog ishlatiladi.
        // Hozirgi Material 3 versiyasi saqlanadi.
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(Date(it)) }
                    showDatePicker = false
                }) { Text("Tanlash", color = primaryColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Bekor qilish", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTimeSelector(selectedDate: Date, onTimeSelected: (Date) -> Unit, containerColor: Color, primaryColor: Color, textColor: Color) {
    var showTimePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { showTimePicker = true },
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = primaryColor)
            Spacer(Modifier.width(8.dp))
            Text(android.text.format.DateFormat.format("HH:mm", selectedDate).toString(), fontWeight = FontWeight.Medium, fontSize = 13.sp, color = textColor)
        }
    }

    if (showTimePicker) {
        // TimePicker dialogini stilini yaxshilash kerak bo'lsa, uning o'rniga Custom Dialog ishlatiladi.
        val calendar = Calendar.getInstance().apply { time = selectedDate }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Vaqtni tanlang", color = MaterialTheme.colorScheme.onSurface) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val newCalendar = Calendar.getInstance().apply { time = selectedDate }
                    newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                    onTimeSelected(newCalendar.time)
                    showTimePicker = false
                }) { Text("Tanlash", color = primaryColor) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Bekor qilish", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        )
    }
}

@Composable
fun PremiumNoteInput(note: String, onNoteChange: (String) -> Unit, colors: TextFieldColors, textColor: Color) {
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("Izoh (ixtiyoriy)", color = textColor.copy(alpha = 0.5f)) },
        leadingIcon = { Icon(Icons.Default.Note, contentDescription = null, tint = primaryAccent) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        textStyle = LocalTextStyle.current.copy(color = textColor)
    )
}