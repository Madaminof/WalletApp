package dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.samandar.walletapp.utils.Strings


@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    title:String,
    text:String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text =title, fontWeight = FontWeight.Bold)
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onTertiary,
        textContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
        text = {
            Text(text)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete()
                    onDismiss()
                }
            ) {
                Text(
                    stringResource(Strings.action_delete),
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Strings.dialog_button_cancel), color = Color.Gray)
            }
        }
    )
}