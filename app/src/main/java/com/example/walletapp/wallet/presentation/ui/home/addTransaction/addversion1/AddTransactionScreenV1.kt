package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1

import CalculatorInput
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1.bottomShetts.AccountSelectorSheet
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1.bottomShetts.CategorySelectorSheet
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionUiState
import kotlinx.coroutines.launch
import rememberCalculatorState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreenV1(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)

    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showCategorySheet by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    val selectedTabIndex by remember(uiState.selectedType) {
        mutableStateOf(if (uiState.selectedType == TransactionType.EXPENSE) 0 else 1)
    }

    val calcState = rememberCalculatorState()

    LaunchedEffect(uiState.amountInput) {
        if (uiState.amountInput != calcState.display) {
            calcState.display = uiState.amountInput.ifEmpty { "0" }
        }
    }
    LaunchedEffect(calcState.display) {
        if (calcState.display != uiState.amountInput) {
            viewModel.onAmountChange(calcState.display)
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
            CenterAlignedTopAppBar(
                title = { Text("Yangi Tranzaksiya", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Yopish", tint = MaterialTheme.colorScheme.onTertiary)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::saveTransaction) {
                        Icon(Icons.Default.Check, contentDescription = "Saqlash", tint = MaterialTheme.colorScheme.onTertiary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onTertiary,
                    titleContentColor = MaterialTheme.colorScheme.onTertiary,
                    actionIconContentColor = MaterialTheme.colorScheme.onTertiary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isLoading || uiState.isSaving) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                TypeAndAmountDisplay(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        val type = if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                        viewModel.onTypeChange(type)
                    },
                    amount = calcState.display // CalculatorState bilan ishlaydi
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            TransactionDetailsPanel(
                uiState = uiState,
                onCategoryClick = { showCategorySheet = true },
                onAccountClick = { showAccountSheet = true },
                onNoteClick = { showNoteDialog = true }
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp).background(MaterialTheme.colorScheme.onBackground))

            // CALCULATOR INPUT
            CalculatorInput(
                calcState = calcState,
                onSaveClick = { viewModel.saveTransaction() },
                isSaveEnabled = uiState.selectedAccount != null && uiState.amountInput.toDoubleOrNull() != null && !uiState.isSaving,
                isSaving = uiState.isSaving
            )
        }

        if (showCategorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showCategorySheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                CategorySelectorSheet(
                    uiState = uiState,
                    selectedTabIndex = selectedTabIndex,
                    onCategorySelected = { category ->
                        viewModel.onCategorySelect(category)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showCategorySheet = false
                        }
                    }
                )
            }
        }

        if (showAccountSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAccountSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer

            ) {
                AccountSelectorSheet(
                    accounts = uiState.accounts,
                    selectedAccount = uiState.selectedAccount,
                    onAccountSelected = { account ->
                        viewModel.onAccountSelect(account)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showAccountSheet = false
                        }
                    }
                )
            }
        }

        if (showNoteDialog) {
            NoteInputDialog(
                currentNote = uiState.note,
                onNoteChange = viewModel::onNoteChange,
                onDismiss = { showNoteDialog = false }
            )
        }
    }
}

@Composable
fun TypeAndAmountDisplay(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    amount: String
) {
    val symbols = DecimalFormatSymbols(Locale.ROOT).apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }
    val formatter = DecimalFormat("#,##0.00", symbols)
    val formattedAmount = try {
        amount.toDoubleOrNull()?.let { formatter.format(it) } ?: "0.00"
    } catch (_: Exception) {
        "0.00"
    }
    val targetColor = if (selectedTabIndex == 0)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.primary

    val displayColor by animateColorAsState(targetValue = targetColor, label = "amount_color")
    var amountFontSize by remember(amount) { mutableStateOf(48.sp) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = {},
            divider = {},
            containerColor = Color.Transparent,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(0.3f))
        ) {
            listOf("Xarajat", "Daromad").forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) displayColor.copy(alpha = 0.8f) else Color.Transparent),
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onTertiary
                        )
                    }
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val displayText = buildAnnotatedString {
                append(formattedAmount)

                addStyle(
                    style = SpanStyle(
                        fontSize = amountFontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = displayColor
                    ),
                    start = 0,
                    end = formattedAmount.length
                )

                append(" UZS")
                addStyle(
                    style = SpanStyle(
                        fontSize = (amountFontSize * 0.5f),
                        fontWeight = FontWeight.Medium,
                        color = displayColor
                    ),
                    start = formattedAmount.length + 1,
                    end = formattedAmount.length + 1 + 3
                )
            }

            Text(
                text = displayText,
                fontSize = amountFontSize,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp, bottom = 4.dp),
                onTextLayout = { result ->
                    if (result.didOverflowWidth) {
                        amountFontSize *= 0.9f
                    }
                }
            )
        }
    }
}

@Composable
fun TransactionDetailsPanel(
    uiState: AddTransactionUiState,
    onCategoryClick: () -> Unit,
    onAccountClick: () -> Unit,
    onNoteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        DetailChip(
            label = uiState.selectedCategory?.name ?: "Kategoriya",
            icon = uiState.selectedCategory?.let { painterResource(id = it.iconResId ?: 0) }
                ?: Icons.Default.Category,
            onClick = onCategoryClick,
            selected = uiState.selectedCategory != null,
            selectedColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        DetailChip(
            label = uiState.selectedAccount?.name ?: "Hisob",
            icon = uiState.selectedAccount?.let { painterResource(id = it.iconResId?:0) }
                ?:Icons.Default.AccountBalanceWallet,
            onClick = onAccountClick,
            selected = uiState.selectedAccount != null,
            selectedColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        DetailChip(
            label = "Eslatma",
            icon = Icons.Default.Note,
            onClick = onNoteClick,
            selected = uiState.note.isNotEmpty(),
            selectedColor = MaterialTheme.colorScheme.primary,
            limitText = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DetailChip(
    label: String,
    icon: Any,
    onClick: () -> Unit,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    limitText: Boolean = false
) {
    val background = if (selected) selectedColor else Color.LightGray.copy(alpha = 0.4f)
    val contentColor = if (selected) Color.White else Color.Black.copy(alpha = 0.8f)

    Card(
        modifier = modifier
            .height(45.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // ICON
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                color = contentColor,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
fun NoteInputDialog(
    currentNote: String,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var noteInput by remember { mutableStateOf(currentNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Eslatma kiritish",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary
            )
        },
        text = {
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = { Text("Eslatmani shu yerga yozing", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                label = { Text("Eslatma", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 150.dp),
                singleLine = false,
                maxLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer

                )

            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onNoteChange(noteInput.trim())
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                enabled = noteInput.isNotBlank()
            ) {
                Text("Saqlash")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Bekor qilish", color = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
            }
        }
    )
}
