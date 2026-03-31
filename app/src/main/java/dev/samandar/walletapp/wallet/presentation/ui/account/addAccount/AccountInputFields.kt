package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat


@Composable
fun AccountInputFields(
    name: String,
    onNameChange: (String) -> Unit,
    isNameExists: Boolean,
    balanceText: String,
    onBalanceChange: (String) -> Unit
) {
    val amountDouble = balanceText.toDoubleOrNull() ?: 0.0
    val formattedText = remember(balanceText) {
        if (balanceText.isEmpty()) ""
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppleTextField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(R.string.add_account_name_input_label),
            placeholder = stringResource(R.string.add_account_name_placeholder),
            isError = isNameExists && name.isNotBlank(),
            errorText = if (isNameExists) stringResource(R.string.add_account_error_name_exists) else null
        )
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
                                onBalanceChange(cleanValue)
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
                                if (balanceText.isEmpty()) {
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

@Composable
fun AppleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    enabled: Boolean = true, // 👈 Yangi parametr
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingText: String? = null,
) {
    val alpha = if (enabled) 1f else 0.5f
    Column(modifier = Modifier.fillMaxWidth().alpha(alpha)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Gray
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = Color.Gray.copy(0.4f))
            },
            singleLine = true,
            isError = isError,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,

                focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                errorContainerColor =  MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),


                disabledTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f), // 👈 Bloklanganda ham o'z rangi qolsin

                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                if (trailingText != null) {
                    Text(
                        text = trailingText,
                        modifier = Modifier.padding(end = 12.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary.copy(0.7f)
                        )
                    )
                }
            }
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }
    }
}