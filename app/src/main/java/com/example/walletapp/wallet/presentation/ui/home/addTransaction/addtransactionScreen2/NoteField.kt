package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFeatureController(
    viewModel: AddTransactionViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val noteText = viewModel.uiState.note
    val accentColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .clickable { showBottomSheet = true }
                .clip(MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (noteText.isNotBlank()) {noteText} else "Add note",
                fontWeight = if (noteText.isNotBlank()) FontWeight.Normal else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (noteText.isNotBlank()) MaterialTheme.colorScheme.onTertiary else accentColor,
                textDecoration = if (noteText.isBlank()) TextDecoration.Underline else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NoteBottomSheetContent(
                    viewModel = viewModel,
                    onSave = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }
                )
            }
        }
    }

}


@Composable
fun NoteBottomSheetContent(
    viewModel: AddTransactionViewModel,
    onSave: () -> Unit
) {
    var noteInput by remember { mutableStateOf(viewModel.uiState.note) }
    val accentColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val originalNote = viewModel.uiState.note

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add note",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            if (noteInput.isNotBlank()) {
                TextButton(
                    onClick = { noteInput = "" },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp) // Ixcham qilish
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Izohni tozalash",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Clear", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            label = { Text("Izohingizni kiriting", color = MaterialTheme.colorScheme.onTertiary.copy(0.2f)) },
            placeholder = { Text("Masalan: Restoran xarajati...", color = MaterialTheme.colorScheme.onTertiary.copy(0.2f)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            shape = MaterialTheme.shapes.medium,
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = accentColor,
                focusedLabelColor = accentColor,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onSave,
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text(
                    "Bekor qilish",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.onNoteChange(noteInput)
                    onSave()
                },
                enabled = noteInput != originalNote,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = onPrimaryColor,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                )
            ) {
                Text("Saqlash", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}