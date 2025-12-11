package com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.wallet.domain.model.ShoppingList

@Composable
fun EditListDialog(
    list: ShoppingList,
    onDismiss: () -> Unit,
    onUpdateList: (String) -> Unit
) {
    var listName by remember { mutableStateOf(list.title) }
    val isNameValid = listName.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)),
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
                Text(
                    text = "Edit list name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List name", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onUpdateList(listName) },
                        enabled = isNameValid,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Save", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}