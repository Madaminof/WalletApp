package com.example.walletapp.wallet.presentation.ui.home.addTransaction

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionUiState
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val focusManager = LocalFocusManager.current

    val selectedTabIndex by remember(uiState.selectedType) {
        mutableStateOf(if (uiState.selectedType == TransactionType.EXPENSE) 0 else 1)
    }
    val amountFocusRequester = remember { FocusRequester() }
    val noteFocusRequester = remember { FocusRequester() }
    LaunchedEffect(uiState.selectedCategory) {
        if (uiState.selectedCategory != null) {
            scope.launch {
                amountFocusRequester.requestFocus()
            }
        }
    }
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccessStatus()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                HeaderAndTabRowOptimized(
                    onBackClick = {
                        viewModel.resetSaveSuccessStatus()
                        navController.popBackStack()
                    },
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        val type =
                            if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                        viewModel.onTypeChange(type)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.onBackground)
        ) {
            if (uiState.isLoading || uiState.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            ) {
                item {
                    CategoryGridSection(
                        uiState = uiState,
                        selectedTabIndex = selectedTabIndex,
                        onCategorySelected = viewModel::onCategorySelect,
                    )
                }
            }
            if (uiState.selectedCategory != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AmountInputField(
                        amount = uiState.amountInput,
                        onAmountChange = viewModel::onAmountChange,
                        focusRequester = amountFocusRequester,
                        onNext = { noteFocusRequester.requestFocus() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AccountSelector(
                        accounts = uiState.accounts,
                        selectedAccount = uiState.selectedAccount,
                        onAccountSelected = viewModel::onAccountSelect
                    )
                    NoteInputField(
                        note = uiState.note,
                        onNoteChange = viewModel::onNoteChange,
                        focusRequester = noteFocusRequester,
                        onDone = { focusManager.clearFocus() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::saveTransaction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        enabled = uiState.selectedAccount != null && uiState.amountInput.toDoubleOrNull() != null && !uiState.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Tranzaksiyani Saqlash", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun HeaderAndTabRowOptimized(
    onBackClick: () -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val accentBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
    val selectedBgColor = Color.Gray.copy(0.1f)
    val selectedTextColor = MaterialTheme.colorScheme.onTertiary
    val unselectedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
    val selectedFontSize = 15.sp
    val unselectedFontSize = 14.sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Orqaga",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            Text(
                "Yangi Tranzaksiya",
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
            )
            Spacer(Modifier.width(48.dp))
        }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = {},
            divider = {},
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            listOf("Xarajat", "Daromad").forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index

                val currentBackgroundColor = if (isSelected) selectedBgColor else Color.Transparent
                val currentBorderColor = if (isSelected) accentBorderColor else Color.Transparent
                val currentTextColor = if (isSelected) selectedTextColor else unselectedTextColor
                val currentFontSize = if (isSelected) selectedFontSize else unselectedFontSize

                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(currentBackgroundColor)
                        .border(
                            width = 1.dp,
                            color = currentBorderColor,
                            shape = RoundedCornerShape(16.dp)
                        ),

                    text = {
                        Text(
                            text = title,
                            fontSize = currentFontSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = currentTextColor
                        )
                    }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
    }
}


@Composable
fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    onNext: () -> Unit
) {
    val symbols = DecimalFormatSymbols(Locale.ROOT).apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }
    val formatter = DecimalFormat("#,##0.##", symbols)
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = amount, selection = TextRange(amount.length)))
    }
    LaunchedEffect(amount) {
        val currentCleanValue = textFieldValueState.text.replace(" ", "").replace(",", "")
        if (currentCleanValue != amount) {
            textFieldValueState = textFieldValueState.copy(text = amount, selection = TextRange(amount.length))
        }
    }
    val finalTextFieldValue by remember(textFieldValueState) {
        derivedStateOf {
            val rawValue = textFieldValueState.text.replace(" ", "").replace(",", "")
            val oldSelection = textFieldValueState.selection.end

            if (rawValue.isEmpty()) {
                return@derivedStateOf TextFieldValue("")
            }

            val parsedNumber = rawValue.toBigDecimalOrNull()

            val formattedText = if (parsedNumber != null) {
                formatter.format(parsedNumber)
            } else {
                rawValue
            }
            val oldFormatSeparatorsCount = textFieldValueState.text.count { it == ' ' || it == ',' }
            val newFormatSeparatorsCount = formattedText.count { it == ' ' || it == ',' }

            val separatorDiff = newFormatSeparatorsCount - oldFormatSeparatorsCount
            val newCursorPosition = (oldSelection + separatorDiff).coerceIn(0, formattedText.length)

            TextFieldValue(formattedText, TextRange(newCursorPosition))
        }
    }

    val modifier = if (focusRequester != null) {
        Modifier.fillMaxWidth().focusRequester(focusRequester)
    } else {
        Modifier.fillMaxWidth()
    }

    OutlinedTextField(
        value = finalTextFieldValue,
        onValueChange = { newValue ->
            val newCleanValue = newValue.text.replace(" ", "").replace(",", "")
            val isValid = newCleanValue.all { it.isDigit() || it == '.' } && newCleanValue.count { it == '.' } <= 1

            if (isValid) {
                textFieldValueState = newValue.copy(text = newCleanValue, selection = newValue.selection)
                onAmountChange(newCleanValue)
            }
        },
        label = { Text("Summa (Amount)", color = Color.Gray) },
        leadingIcon = {
            Icon(
                Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun NoteInputField(
    note: String,
    onNoteChange: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    onDone: () -> Unit
) {
    val modifier = if (focusRequester != null) {
        Modifier.fillMaxWidth().focusRequester(focusRequester)
    } else {
        Modifier.fillMaxWidth()
    }

    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("Eslatma (Note)", color = Color.Gray.copy(0.5f)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 2,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccountSelector(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (accounts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = "No accounts",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Mavjud hisoblar yo'q. Iltimos, birinchi hisob qo'shing.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                accounts.forEach { account ->
                    val isSelected = selectedAccount?.id == account.id

                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(0.1f) else MaterialTheme.colorScheme.surface,
                        animationSpec = tween(durationMillis = 200), label = "background_color"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        animationSpec = tween(durationMillis = 200), label = "border_color"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        animationSpec = tween(durationMillis = 200), label = "content_color"
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor
                        ),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 8.dp, bottom = 8.dp)
                            .clickable(
                                onClick = { onAccountSelected(account) }
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (account.name == "Cash") {
                                    Icons.Default.AccountBalanceWallet
                                } else {
                                    Icons.Default.CreditCard
                                },
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = account.name,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = contentColor
                            )
                            if (isSelected) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCategory(category: Category, isSelected: Boolean, onClick: () -> Unit){
    val interactionSource = remember { MutableInteractionSource() }

    val expenseColor = MaterialTheme.colorScheme.error
    val incomeColor = MaterialTheme.colorScheme.primary

    val baseColor = if (category.type == TransactionType.EXPENSE) expenseColor else incomeColor

    val boxColor = if (isSelected) baseColor else MaterialTheme.colorScheme.onTertiary.copy(0.1f)
    val iconColor = if (isSelected) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    Column(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = boxColor, shape = CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (category.iconResId != null) {
                // Eslatma: painterResource yuklanishi uchun sizning R resurslaringiz mavjud bo'lishi kerak.
                // Bu joyda xato yuzaga kelishi mumkin, agar R resurslari qo'shilmagan bo'lsa.
                Icon(
                    painter = painterResource(id = category.iconResId),
                    contentDescription = category.name,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    Icons.Default.Category,
                    contentDescription = category.name,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) baseColor else MaterialTheme.colorScheme.onTertiary.copy(0.7f),
            maxLines = 1
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun CategoryGridSection(
    uiState: AddTransactionUiState,
    selectedTabIndex: Int,
    onCategorySelected: (Category) -> Unit
) {
    val currentGridList = if (selectedTabIndex == 0) uiState.expenseCategories else uiState.incomeCategories

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth().heightIn(max = 1000.dp)
        ) {
            if (currentGridList.isEmpty()) {
                item(span = { GridItemSpan(4) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Kategoriyalar mavjud emas. Iltimos, qo'shing.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(currentGridList, key = { it.id ?: it.name }) { item ->
                    val isSelected = uiState.selectedCategory?.id == item.id
                    ItemCategory(
                        category = item,
                        isSelected = isSelected,
                        onClick = {
                            onCategorySelected(item)
                        }
                    )
                }
            }
        }
}