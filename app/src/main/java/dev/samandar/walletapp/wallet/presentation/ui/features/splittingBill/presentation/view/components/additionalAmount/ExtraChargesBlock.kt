package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.additionalAmount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R


@Composable
fun ExtraChargesBlock(
    serviceChargePercent: String,
    onServiceChange: (String) -> Unit,
    taxPercent: String,
    onTaxChange: (String) -> Unit,
    discountAmount: String,
    onDiscountChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.extra_charges),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 15.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Service Charge (%)
            ChargeSmallInput(
                value = serviceChargePercent,
                onValueChange = onServiceChange,
                label = stringResource(R.string.service_charge),
                modifier = Modifier.weight(1f),
            )

            // Tax (%)
            ChargeSmallInput(
                value = taxPercent,
                onValueChange = onTaxChange,
                label = stringResource(R.string.tax_charge),
                modifier = Modifier.weight(1f),
            )

            // Discount (Summa)
            ChargeSmallInput(
                value = discountAmount,
                onValueChange = onDiscountChange,
                label = stringResource(R.string.discount_label),
                modifier = Modifier.weight(1.2f),
            )
        }
    }
}

@Composable
fun ChargeSmallInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal
    ),
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onValueChange(it) },
        label = { Text(label, fontSize = 11.sp, color = Color.Gray.copy(0.5f)) },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Gray.copy(0.3f),
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}