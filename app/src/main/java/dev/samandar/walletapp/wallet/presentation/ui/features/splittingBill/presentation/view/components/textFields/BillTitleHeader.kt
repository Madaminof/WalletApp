package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.textFields


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings


@Composable
fun BillTitleHeader(
    title: String,
    onTitleChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                label = stringResource(Strings.bill_name_label),
                placeholder = stringResource(Strings.bill_name_placeholder),
                isError = false,
                errorText = null,
                trailingText = "${title.length}/30"
            )
        }
    }
}


@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Done
    ),
    trailingText: String? = null,
) {
    val focusManager = LocalFocusManager.current
    val alpha = if (enabled) 1f else 0.5f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Gray,
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        TextField(
            value = value,
            onValueChange = {
                if (it.length <= 30) onValueChange(it)
            },
            placeholder = {
                Text(placeholder, color = Color.Gray.copy(alpha = 0.4f), fontSize = 15.sp)
            },
            singleLine = true,
            isError = isError,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,

                focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.025f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.025f),
                disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f),
                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),

                focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                disabledTextColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                if (trailingText != null) {
                    Text(
                        text = trailingText,
                        modifier = Modifier.padding(end = 12.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isError) MaterialTheme.colorScheme.error else Color.Gray
                        )
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = isError && errorText != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorText ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }
    }
}