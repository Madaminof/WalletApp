package com.example.walletapp.wallet.presentation.ui.account

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.R
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.wallet.domain.model.Account
import kotlinx.coroutines.launch
import java.util.UUID

// Helper funksiya: rangning kontrastini aniqlash (Text rangini avtomatik tanlash uchun)
private fun isLightColor(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b)
    return luminance > 0.5
}

// =================================================================================================
// 1. ASOSIY SCREEN FUNKSIYASI (UI MANTIQINI BOSHQA FUNKSIYALARGA DELEGATSIYA QILADI)
// =================================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    navController: NavController,
    onSave: (Account) -> Unit,
    existingAccounts: List<Account>
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State Variables
    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("0") }
    var selectedColor by remember { mutableStateOf(Color(0xFF1976D2)) }
    var selectedIcon by remember { mutableStateOf(R.drawable.ic_card_default) }

    // Validation Logic
    val trimmedName = name.trim()
    val isNameBlank = trimmedName.isEmpty()
    val isNameExists = existingAccounts.any { it.name.equals(trimmedName, ignoreCase = true) && trimmedName.isNotEmpty() }
    val canSave = !isNameBlank && !isNameExists

    // UI Animations & Dynamic Colors
    val previewColor by animateColorAsState(targetValue = selectedColor, label = "PreviewColorAnimation")
    val previewTextColor = if (isLightColor(previewColor)) Color.Black.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)

    // Data lists
    val availableColors = remember {
        listOf(
            Color(0xFF1976D2), Color(0xFF0F9915), Color(0xFFFF9800),
            Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF795548),
            Color(0xFF009688), Color(0xFFB0BEC5), Color(0xFFFDD835)
        )
    }
    val availableIcons = remember {
        listOf(
            R.drawable.ic_card_default, R.drawable.ic_visa, R.drawable.ic_mastercard,
            R.drawable.ic_wallet, R.drawable.ic_dollor
        )
    }

    // Saqlash mantiqi
    val handleSave: () -> Unit = {
        focusManager.clearFocus()
        if (!canSave) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = if (isNameBlank) "Iltimos, hamyon nomini kiriting" else "Bu nom avval ishlatilgan"
                )
            }
        } else {
            val balance = balanceText.toDoubleOrNull() ?: 0.0
            val colorHex = "#" + Integer.toHexString(previewColor.toArgb()).uppercase().substring(2)
            val account = Account(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                initialBalance = balance,
                colorHex = colorHex,
                iconResId = selectedIcon
            )
            onSave(account)
            navController.popBackStack()
        }
    }

    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)
    Scaffold(
        topBar = {
            AddAccountTopBar(navController = navController, canSave = canSave, onSave = handleSave)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            // 2. Preview Card
            AccountPreviewCard(
                name = trimmedName,
                balanceText = balanceText,
                selectedIcon = selectedIcon,
                previewColor = previewColor,
                previewTextColor = previewTextColor
            )

            // Divider
            Divider(modifier = Modifier.padding(vertical = 4.dp).background(Color.Gray))

            // 3. Input Fields
            AccountInputFields(
                name = name,
                onNameChange = { new ->
                    val filtered = new.filter { it.isLetterOrDigit() || it.isWhitespace() }
                    if (filtered.length <= 30) name = filtered
                },
                isNameExists = isNameExists,
                balanceText = balanceText,
                onBalanceChange = { new ->
                    if (new.count { it == '.' } <= 1 && new.all { it.isDigit() || it == '.' }) {
                        balanceText = new
                    }
                }
            )

            // 4. Color Selector
            ColorSelector(
                availableColors = availableColors,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            // 5. Icon Selector
            IconSelector(
                availableIcons = availableIcons,
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )

            Spacer(modifier = Modifier.height(32.dp)) // Pastki Padding
        }
    }
}

// =================================================================================================
// 2. KOMPONENTLARGA AJRATILGAN QISMLAR
// =================================================================================================

// TopBar komponenti
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountTopBar(
    navController: NavController,
    canSave: Boolean,
    onSave: () -> Unit,

) {
    TopAppBar(
        title = { Text("Yangi Hamyon", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Orqaga")
            }
        },
        actions = {
            TextButton(
                onClick = onSave,
                enabled = canSave,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Saqlash", fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}

// Preview Card komponenti
@Composable
fun AccountPreviewCard(
    name: String,
    balanceText: String,
    selectedIcon: Int,
    previewColor: Color,
    previewTextColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = previewColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(previewTextColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = selectedIcon),
                    contentDescription = "Selected icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (name.isNotEmpty()) name else "Hamyon nomi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = previewTextColor.copy(alpha = 1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${balanceText.ifBlank { "0" }} UZS",
                    style = MaterialTheme.typography.titleMedium,
                    color = previewTextColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Input Fields komponenti
@Composable
fun AccountInputFields(
    name: String,
    onNameChange: (String) -> Unit,
    isNameExists: Boolean,
    balanceText: String,
    onBalanceChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Hamyon nomi (Modern Filled TextField)
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Hamyon nomi *") },
            isError = isNameExists,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(0.3f)
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                if (isNameExists && name.isNotBlank()) {
                    Text("Bu nom oldin ishlatilgan", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        TextField(
            value = balanceText,
            onValueChange = onBalanceChange,
            label = { Text("Balans (majburiy emas)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(0.3f)
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                Text("Masalan: 150000.50", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        )
    }
}

// Rang tanlash komponenti
@Composable
fun ColorSelector(
    availableColors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Text("Rang tanlang", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(availableColors) { c ->
            val isSelected = c == selectedColor
            Box(
                modifier = Modifier
                    .size(56.dp) // O'lcham bir xil, tanlanganda kattalashmasin (chiroyliroq)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(c) }
            )
        }
    }
}

// Icon tanlash komponenti
@Composable
fun IconSelector(
    availableIcons: List<Int>,
    selectedIcon: Int,
    onIconSelected: (Int) -> Unit
) {
    Text("Icon tanlang", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(availableIcons) { iconRes ->
            val selected = iconRes == selectedIcon
            Box(
                modifier = Modifier
                    .size(60.dp) // O'lcham kattalashtirildi
                    .clip(RoundedCornerShape(12.dp))
                    // ⭐️ Tanlanganda primary, tanlanmaganda Surface/Background (kontrastni oshirish)
                    .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    .clickable { onIconSelected(iconRes) }
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, // Tanlanmaganda nozik outline
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "icon $iconRes",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}