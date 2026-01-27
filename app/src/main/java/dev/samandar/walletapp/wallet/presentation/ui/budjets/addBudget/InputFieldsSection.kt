package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat


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
    val categoryName = getTranslatedName(selectedCategory?.name ?: stringResource(R.string.category_label_placeholder))

    val formattedText = remember(maxAmountInput) {
        if (maxAmountInput.isEmpty()) ""
        else AmountFormat(amountDouble, includeFraction = false)
    }

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = formattedText)) }

    LaunchedEffect(formattedText) {
        if (textFieldValueState.text != formattedText) {
            textFieldValueState = textFieldValueState.copy(
                text = formattedText,
                selection = TextRange(formattedText.length)
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Surface(
            onClick = onCategoryClick,
            shape = RoundedCornerShape(16.dp),
            color = if (isCategorySelected) Color(selectedCategory!!.colorArgb).copy(0.1f) else MaterialTheme.colorScheme.onTertiary.copy(0.03f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isCategorySelected) Color(selectedCategory!!.colorArgb).copy(0.15f)
                            else primaryColor.copy(0.08f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(selectedCategory?.iconResId ?: R.drawable.ic_add),
                        contentDescription = null,
                        tint = if (isCategorySelected) Color(selectedCategory!!.colorArgb) else primaryColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.category_label_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                    )
                    Text(
                        text = categoryName.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                }

                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.add_account_balance_input_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = textFieldValueState,
                        onValueChange = { newValue ->
                            val cleanValue = newValue.text.replace("[^\\d.]".toRegex(), "")
                            if (cleanValue.length <= 12) {
                                onAmountChange(cleanValue)
                                textFieldValueState = newValue
                            }
                        },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (maxAmountInput.isEmpty()) {
                                    Text("0", color = MaterialTheme.colorScheme.onTertiary.copy(0.15f))
                                }
                                innerTextField()
                            }
                        }
                    )
                    Text(
                        text = activeCurrency,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}