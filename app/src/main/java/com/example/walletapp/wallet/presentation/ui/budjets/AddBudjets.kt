package com.example.walletapp.wallet.presentation.ui.budjets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.R
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.wallet.domain.model.Budget
import com.example.walletapp.wallet.domain.model.BudgetPeriod
import com.example.walletapp.wallet.domain.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}

private fun formatAmount(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("uz", "UZ"))
    format.currency = java.util.Currency.getInstance("UZS")
    return format.format(amount).replace("UZS", "").trim()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudjetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val expenseCategories by viewModel.expenseCategories.collectAsState()

    // --- Holatni boshqarish (State Management) ---
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var maxAmountInput by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }

    // UI holatlari
    val openStartDateDialog = remember { mutableStateOf(false) }
    val openEndDateDialog = remember { mutableStateOf(false) }
    val showCategorySheet = remember { mutableStateOf(false) }

    val colorFalse = MaterialTheme.colorScheme.onBackground
    val colorTrue = MaterialTheme.colorScheme.primary


    val categoryColor = remember(selectedCategory) {
        if (selectedCategory != null) {
            colorTrue
        } else {
            colorFalse
        }
    }
    val animatedColor by animateColorAsState(targetValue = categoryColor, label = "BudgetColorAnimation")

    if (showCategorySheet.value) {
        CategorySelectionBottomSheet(
            expenseCategories = expenseCategories,
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = category
                showCategorySheet.value = false
            },
            onDismiss = { showCategorySheet.value = false }
        )
    }
    if (openStartDateDialog.value) {
        val startDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { openStartDateDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let {
                            startDateMillis = it
                        }
                        openStartDateDialog.value = false
                    }
                ) {
                    Text("Tanlash")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (openEndDateDialog.value) {
        val endDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { openEndDateDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let {
                            endDateMillis = it
                        }
                        openEndDateDialog.value = false
                    }
                ) {
                    Text("Tanlash")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AddBudgetTopBar(
                navController = navController,
                selectedCategory = selectedCategory,
                maxAmountInput = maxAmountInput,
                selectedPeriod = selectedPeriod,
                endDateMillis = endDateMillis,
                startDateMillis = startDateMillis,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                scope = scope
                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            BudgetPreviewCard(
                categoryName = selectedCategory?.name,
                maxAmount = maxAmountInput.toDoubleOrNull() ?: 0.0,
                period = selectedPeriod,
                color = animatedColor
            )

            // 2. Input Fields (Category & Amount)
            InputFieldsSection(
                selectedCategory = selectedCategory,
                onCategoryClick = { showCategorySheet.value = true },
                maxAmountInput = maxAmountInput,
                onAmountChange = { newValue ->
                    maxAmountInput = newValue.filter { char -> char.isDigit() || (char == '.' && !maxAmountInput.contains('.')) }
                }
            )

            // 3. Budjet Davri (Period) tanlash
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { period ->
                    selectedPeriod = period
                    if (period != BudgetPeriod.CUSTOM) {
                        endDateMillis = null
                    }
                }
            )

            // 4. Sanalarni kiritish (faqat CUSTOM uchun)
            if (selectedPeriod == BudgetPeriod.CUSTOM) {
                DateRangeSelector(
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    onStartDateClick = { openStartDateDialog.value = true },
                    onEndDateClick = { openEndDateDialog.value = true }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// =================================================================================================
// KOMPONENTLAR: BOTTOM SHEET (GRID)
// =================================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionBottomSheet(
    expenseCategories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
            Text(
                "Kategoriya tanlang",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.onTertiary
            )
            Divider(modifier = Modifier.padding(bottom = 8.dp))

            if (expenseCategories.isEmpty()) {
                Text(
                    "Kategoriyalar topilmadi.",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(expenseCategories) { category ->
                        val isSelected = selectedCategory == category
                        CategoryGridItem(
                            category = category,
                            isSelected = isSelected,
                            onCategoryClick = { onCategorySelected(category) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CategoryGridItem(
    category: Category,
    isSelected: Boolean,
    onCategoryClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onTertiary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceContainer = MaterialTheme.colorScheme.onBackground

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) primaryContainer else surfaceContainer,
        label = "CategoryContainerColor"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) primaryColor else onSurfaceColor.copy(0.7f),
        label = "CategoryIconTint"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else onSurfaceColor,
        label = "CategoryTextColor"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable(onClick = onCategoryClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        border = if (isSelected) BorderStroke(2.dp, primaryColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(category.iconResId ?: R.drawable.ic_home),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
                lineHeight = 12.sp
            )
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Tanlangan",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetTopBar(
    navController: NavController,
    selectedCategory: Category?,
    maxAmountInput: String,
    selectedPeriod: BudgetPeriod,
    endDateMillis: Long?,
    startDateMillis: Long,
    viewModel: BudgetViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
    ) {
    TopAppBar(
        title = { Text("Yangi Budjet", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Orqaga")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        actions = {
            SaveButton(
                selectedCategory = selectedCategory,
                maxAmountInput = maxAmountInput,
                selectedPeriod = selectedPeriod,
                endDateMillis = endDateMillis,
                startDateMillis = startDateMillis,
                viewModel = viewModel,
                navController = navController,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
    )
}

@Composable
fun BudgetPreviewCard(
    categoryName: String?,
    maxAmount: Double,
    period: BudgetPeriod,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        val contentColor = if (color == MaterialTheme.colorScheme.onBackground) Color.Gray.copy(0.3f) else Color.White
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Budjet (Expense)",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )

            Text(
                text = categoryName ?: "Kategoriya tanlanmagan",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
                fontSize = 26.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${formatAmount(maxAmount)} UZS",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = contentColor,
                    fontSize = 20.sp

                )
                Text(
                    text = period.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor.copy(alpha = 0.8f),
                    fontSize = 11.sp

                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldsSection(
    selectedCategory: Category?,
    onCategoryClick: () -> Unit,
    maxAmountInput: String,
    onAmountChange: (String) -> Unit
) {
    val isCategorySelected = selectedCategory != null
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            onClick = onCategoryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = if (isCategorySelected) 2.dp else 1.dp,
                color = if (isCategorySelected) primaryColor else MaterialTheme.colorScheme.outlineVariant
            ),
            color = if (isCategorySelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent, // Tanlanganda fon ochiqroq bo'ladi
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(selectedCategory?.iconResId?:R.drawable.ic_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Kategoriya",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        )
                        Text(
                            text = selectedCategory?.name ?: "Kategoriyani tanlang",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isCategorySelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
                Icon(
                    Icons.Filled.ArrowForwardIos,
                    contentDescription = "Kategoriyalarga o'tish",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        OutlinedTextField(
            value = maxAmountInput,
            onValueChange = onAmountChange,
            label = { Text("Maksimal Budjet Summasi") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp),
            suffix = { Text("UZS", fontWeight = FontWeight.SemiBold) }
        )
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: BudgetPeriod,
    onPeriodSelected: (BudgetPeriod) -> Unit
) {
    Column {
        Text(
            "Budjet Davrini tanlang:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onTertiary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BudgetPeriod.entries.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(period.name) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.background,

                    ),
                )
            }
        }
    }
}

@Composable
fun DateRangeSelector(
    startDateMillis: Long,
    endDateMillis: Long?,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Column {
        Text(
            "Maxsus Budjet Oralig'i:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onTertiary
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DateInputField(
                value = startDateMillis.toDateString(),
                label = "Boshlanish Sanasi",
                onClick = onStartDateClick,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            DateInputField(
                value = endDateMillis?.toDateString() ?: "Tugash sanasi",
                label = "Tugash Sanasi",
                onClick = onEndDateClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Sana tanlash")
            }
        },
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SaveButton(
    selectedCategory: Category?,
    maxAmountInput: String,
    selectedPeriod: BudgetPeriod,
    endDateMillis: Long?,
    startDateMillis: Long,
    viewModel: BudgetViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    TextButton(
        onClick = {
            val amount = maxAmountInput.toDoubleOrNull()

            if (selectedCategory == null) {
                scope.launch { snackbarHostState.showSnackbar("Iltimos, kategoriyani tanlang.") }
                return@TextButton
            }
            if (amount == null || amount <= 0) {
                scope.launch { snackbarHostState.showSnackbar("Maksimal summa 0 dan katta bo'lishi kerak.") }
                return@TextButton
            }

            if (selectedPeriod == BudgetPeriod.CUSTOM && endDateMillis == null) {
                scope.launch { snackbarHostState.showSnackbar("Maxsus budjet uchun tugash sanasini tanlang.") }
                return@TextButton
            }

            if (selectedPeriod == BudgetPeriod.CUSTOM && endDateMillis != null && endDateMillis!! <= startDateMillis) {
                scope.launch { snackbarHostState.showSnackbar("Tugash sanasi boshlanish sanasidan keyin bo'lishi kerak.") }
                return@TextButton
            }

            val newBudget = Budget(
                id = UUID.randomUUID().toString(),
                category = selectedCategory,
                maxAmount = amount,
                period = selectedPeriod,
                startDate = startDateMillis,
                endDate = if (selectedPeriod == BudgetPeriod.CUSTOM) endDateMillis else null,
                isActive = true
            )

            viewModel.saveBudget(newBudget)
            navController.popBackStack()
        },
        modifier = Modifier.padding(end = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("Saqlash", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}