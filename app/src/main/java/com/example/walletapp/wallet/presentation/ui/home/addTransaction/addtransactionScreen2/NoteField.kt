package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel


@Composable
fun NoteFeatureController(
    viewModel: AddTransactionViewModel
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    val noteText = viewModel.uiState.note.takeIf { !it.isNullOrBlank() }
    val accentColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .clickable { showNoteDialog = true }
                .clip(MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = noteText ?: "Add Note",
                fontWeight = if (noteText.isNullOrBlank()) FontWeight.Medium else FontWeight.Normal,
                fontSize = 12.sp,
                color = if (noteText.isNullOrBlank()) accentColor else MaterialTheme.colorScheme.onTertiary,
                textDecoration = if (noteText.isNullOrBlank()) TextDecoration.Underline else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (showNoteDialog) {
            Dialog(onDismissRequest = { showNoteDialog = false }) {
                NoteDialogContent(
                    viewModel = viewModel,
                    onDismiss = { showNoteDialog = false }
                )
            }
        }
    }
}

@Composable
fun NoteDialogContent(
    viewModel: AddTransactionViewModel,
    onDismiss: () -> Unit
) {
    var noteInput by remember { mutableStateOf(viewModel.uiState.note) }
    val accentColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val originalNote = viewModel.uiState.note

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    modifier = Modifier.width(40.dp).clip(CircleShape),
                    thickness = 4.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add Note",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("Izohingizni kiriting", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                placeholder = { Text("Masalan: Restoran xarajati...", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = accentColor,
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    focusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        "Bekor qilish",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.onNoteChange(noteInput)
                        onDismiss()
                    },
                    enabled = noteInput != originalNote,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = onPrimaryColor,
                        disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                        disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Saqlash", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}