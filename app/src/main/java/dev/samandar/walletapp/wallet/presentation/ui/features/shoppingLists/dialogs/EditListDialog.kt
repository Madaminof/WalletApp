package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.dialogs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.ShoppingList

@Composable
fun EditListDialog(
    list: ShoppingList,
    onDismiss: () -> Unit,
    onUpdateList: (String) -> Unit
) {
    var listName by remember { mutableStateOf(list.title) }
    val isNameValid = listName.isNotBlank()

    // Premium animatsiya holati
    var isAnimate by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (isAnimate) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "EditMasterAnim"
    )

    LaunchedEffect(Unit) { isAnimate = true }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true, // Toza markazlash va barqarorlik
            decorFitsSystemWindows = true  // Klaviatura yuqoriga surishi uchun
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .graphicsLayer {
                    // Bizning silliq zoom effektimiz
                    scaleX = 0.95f + (0.05f * animProgress)
                    scaleY = 0.95f + (0.05f * animProgress)
                    alpha = animProgress
                    // Edit dialog odatda markazdan yoki o'ngdan chiqadi
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 0.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Handle
                Box(
                    modifier = Modifier
                        .size(32.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(0.4f))
                )

                Text(
                    text = stringResource(R.string.dialog_edit_list_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                // Premium Soft-UI Input
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.dialog_label_list_name),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiary
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.dialog_button_cancel),
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onUpdateList(listName) },
                        enabled = isNameValid,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                        )
                    ) {
                        Text(
                            stringResource(R.string.dialog_button_save),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}