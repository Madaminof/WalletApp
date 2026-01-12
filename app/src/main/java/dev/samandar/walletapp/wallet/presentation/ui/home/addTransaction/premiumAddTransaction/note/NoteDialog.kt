package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel

@Composable
fun NoteDialogContent(
    viewModel: AddTransactionViewModel,
    onDismiss: () -> Unit
) {
    var noteInput by remember { mutableStateOf(viewModel.uiState.note ?: "") }
    val originalNote = viewModel.uiState.note ?: ""
    val isChanged = noteInput != originalNote

    val charLimit = 100

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 40.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = stringResource(Strings.add_note_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteInput,
                onValueChange = { if (it.length <= charLimit) noteInput = it },
                placeholder = {
                    Text(
                        stringResource(Strings.add_note_placeholder),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 130.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                supportingText = {
                    Text(
                        text = "${noteInput.length} / $charLimit",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (noteInput.length >= charLimit) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        stringResource(Strings.add_note_btn_txt_cancel),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.4f),
                            fontWeight = FontWeight.SemiBold
                        ),
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        viewModel.onNoteChange(noteInput)
                        onDismiss()
                    },
                    enabled = isChanged,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.12f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 4.dp)
                ) {
                    Text(
                        stringResource(Strings.add_note_btn_txt_save),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}