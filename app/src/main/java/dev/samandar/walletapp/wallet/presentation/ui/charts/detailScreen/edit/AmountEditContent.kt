package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmountEditContent(
    initialAmount: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(initialAmount.toLong().toString()) }

    val currentAmount = amountText.toDoubleOrNull() ?: 0.0
    val isChanged = currentAmount != initialAmount && amountText.isNotEmpty()

    val quickAmounts = listOf(5000, 10000, 50000, 100000)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = activeCurrency,
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                BasicTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.length <= 12 && input.all { char -> char.isDigit() }) {
                            amountText = input.removePrefix("0").ifEmpty { "0" }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-1).sp,
                        fontSize = 32.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    visualTransformation = { text ->
                        val originalText = text.text
                        val formatted = AmountFormat(originalText.toDoubleOrNull() ?: 0.0)

                        val amountOffsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int {
                                return formatted.length
                            }

                            override fun transformedToOriginal(offset: Int): Int {
                                return originalText.length
                            }
                        }

                        TransformedText(
                            text = AnnotatedString(formatted),
                            offsetMapping = amountOffsetMapping
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            maxItemsInEachRow = 2
        ) {
            quickAmounts.forEach { value ->
                Surface(
                    onClick = {
                        val current = amountText.toLongOrNull() ?: 0L
                        amountText = (current + value).toString()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.02f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "+ ${AmountFormat(value.toDouble())}",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        IconButton(
            onClick = {
                if (amountText.length > 1) amountText = amountText.dropLast(1)
                else amountText = "0"
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }

        DialogActionButtons(
            onDismiss = onDismiss,
            onSave = { onSave(amountText.toDoubleOrNull() ?: 0.0) },
            saveEnabled = isChanged
        )
    }
}