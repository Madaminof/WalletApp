package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.Strings


@Composable
fun NoteEditContent(
    initialNote: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialNote ?: "") }
    val isChanged = text.trim() != (initialNote ?: "").trim()
    val maxLength = 200

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= maxLength) text = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp),
            placeholder = {
                Text(
                    text = stringResource(Strings.add_note_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f)
                )
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = { text = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.02f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.02f),
                focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                focusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        DialogActionButtons(
            onDismiss = onDismiss,
            onSave = { onSave(text.trim()) },
            saveEnabled = isChanged
        )
    }
}