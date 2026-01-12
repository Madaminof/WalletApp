package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat
import dev.samandar.walletapp.wallet.presentation.utils.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.utils.cleanAmountInput


@Composable
fun InputFieldsSection(
    selectedCategory: Category?,
    onCategoryClick: () -> Unit,
    maxAmountInput: String,
    onAmountChange: (String) -> Unit
) {
    val isCategorySelected = selectedCategory != null
    val primaryColor = MaterialTheme.colorScheme.primary
    val amountDouble = maxAmountInput.toDoubleOrNull() ?: 0.0
    val categoryNmae = getTranslatedName(selectedCategory?.name ?: stringResource(R.string.category_label_placeholder))

    val formattedText = remember(maxAmountInput) {
        if (maxAmountInput.isEmpty()) ""
        else AmountFormat(amountDouble, includeFraction = false)
    }

    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = formattedText))
    }

    LaunchedEffect(formattedText) {
        if (textFieldValueState.text != formattedText) {
            textFieldValueState = textFieldValueState.copy(
                text = formattedText,
                selection = TextRange(formattedText.length)
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Card(
            onClick = onCategoryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCategorySelected)
                    primaryColor.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)
            ),
            border = BorderStroke(
                width = 1.5.dp,
                color = if (isCategorySelected) primaryColor.copy(0.4f)
                else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isCategorySelected) Color(selectedCategory!!.colorArgb).copy(0.15f)
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(selectedCategory?.iconResId ?: R.drawable.ic_add),
                        contentDescription = null,
                        tint = if (isCategorySelected) Color.Unspecified
                        else primaryColor.copy(0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.category_label_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = categoryNmae.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                }

                Icon(
                    Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.input_label_max_amount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = textFieldValueState,
                onValueChange = { newValue ->
                    val cleanValue = cleanAmountInput(newValue.text)
                    if (cleanValue.length <= 12) {
                        onAmountChange(cleanValue)
                        textFieldValueState = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                ),
                placeholder = {
                    Text(
                        "0",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.cash2),
                        contentDescription = null,
                        tint = primaryColor.copy(0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                suffix = {
                    Text(
                        text = getCurrencySymbol(activeCurrency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = primaryColor.copy(0.05f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                    cursorColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    focusedTextColor = primaryColor
                )
            )
        }
    }
}