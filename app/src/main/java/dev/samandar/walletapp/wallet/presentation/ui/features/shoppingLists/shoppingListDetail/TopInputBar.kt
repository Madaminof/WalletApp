package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat

@Composable
fun TopInputBar(
    newItemName: String,
    onNameChange: (String) -> Unit,
    newItemPrice: String,
    onPriceChange: (String) -> Unit,
    onAddItem: () -> Unit,
    focusRequester: FocusRequester
) {
    val priceValue = newItemPrice.toDoubleOrNull() ?: 0.0
    val isEnabled = newItemName.isNotBlank() && priceValue > 0

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                TextField(
                    value = newItemName,
                    onValueChange = onNameChange,
                    placeholder = { Text(stringResource(R.string.input_label_product_name),color = MaterialTheme.colorScheme.onTertiary.copy(0.3f), fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        cursorColor = MaterialTheme.colorScheme.primary,

                        ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                )

                TextField(
                    value = newItemPrice,
                    onValueChange = { input ->
                        val cleanInput = input.replace(Regex("[^0-9]"), "")
                        if (cleanInput.length <= 12) {
                            onPriceChange(cleanInput)
                        }
                    },
                    visualTransformation = AmountVisualTransformation(includeFraction = false),
                    placeholder = { Text(stringResource(R.string.input_label_price), color = MaterialTheme.colorScheme.onTertiary.copy(0.3f), fontSize = 12.sp) },
                    suffix = {
                        if (newItemPrice.isNotEmpty()) {
                            Text(
                                text = activeCurrency,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FloatingActionButton(
                onClick = { if (isEnabled) onAddItem() },
                containerColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.3f),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = if (isEnabled) 4.dp else 0.dp
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    }
}

class AmountVisualTransformation(
    private val includeFraction: Boolean = false
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        // 1. Sening funksiyangdan foydalanamiz
        val number = originalText.toDoubleOrNull() ?: 0.0
        val formattedText = AmountFormat(amount = number, includeFraction = includeFraction)

        // 2. Kursor mantiqi (Kursor sakrab qolmasligi uchun)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                // Formatlangan matndagi raqamlar sonini hisoblab, kursor o'rnini topamiz
                var transformedOffset = 0
                var originalOffset = 0
                while (originalOffset < offset && transformedOffset < formattedText.length) {
                    if (formattedText[transformedOffset].isDigit() || formattedText[transformedOffset] == '.' || formattedText[transformedOffset] == ',') {
                        if (formattedText[transformedOffset].isDigit()) originalOffset++
                    }
                    transformedOffset++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                return originalText.length // Sodda va xatosiz qaytarish
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}