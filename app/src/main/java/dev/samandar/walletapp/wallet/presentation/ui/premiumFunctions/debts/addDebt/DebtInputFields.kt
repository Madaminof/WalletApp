package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.addDebt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency


@Composable
fun DebtInputFields(
    personName: String,
    onNameChange: (String) -> Unit,
    amountText: String,
    onAmountChange: (String) -> Unit,
    accentColor: Color
) {

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = personName,
            onValueChange = onNameChange,
            placeholder = {
                Text(
                    stringResource(R.string.label_name),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.4f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = debtTextFieldColors(accentColor),
            leadingIcon = {
                Icon(Icons.Default.Person, null, tint = accentColor, modifier = Modifier.size(22.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        TextField(
            value = amountText,
            onValueChange = { input ->
                if (input.count { it == '.' } <= 1 && input.all { it.isDigit() || it == '.' }) {
                    onAmountChange(input)
                }
            },
            placeholder = {
                Text("0", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onTertiary.copy(0.4f))
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = AmountVisualTransformation(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 24.sp
            ),
            colors = debtTextFieldColors(accentColor),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            suffix = {
                Text(
                    text = activeCurrency,
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            singleLine = true
        )
    }

}


@Composable
fun debtTextFieldColors(accentColor: Color) = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = accentColor,
    focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
    focusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
)

class AmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val amount = originalText.toDoubleOrNull() ?: 0.0
        val formatted = AmountFormat(amount, includeFraction = originalText.contains("."))

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val offsetFromEnd = originalText.length - offset
                val transformedOffset = formatted.length - offsetFromEnd
                return transformedOffset.coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val offsetFromEnd = formatted.length - offset
                val originalOffset = originalText.length - offsetFromEnd
                return originalOffset.coerceIn(0, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}