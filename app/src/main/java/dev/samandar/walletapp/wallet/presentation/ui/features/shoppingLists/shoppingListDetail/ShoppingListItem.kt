import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

@Composable
fun ShoppingListItem(
    modifier: Modifier = Modifier,
    item: ShoppingItem,
    onToggleChecked: (String) -> Unit,
    onEdit: (ShoppingItem) -> Unit,
    onDelete: (String) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val animatedBgColor by animateColorAsState(
        targetValue = if (item.isChecked) MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f)
        else Color.Transparent,
        label = "bgColor"
    )

    val animatedContentAlpha by animateFloatAsState(
        targetValue = if (item.isChecked) 0.5f else 1f,
        label = "contentAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBgColor)
            .clickable { onToggleChecked(item.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .alpha(animatedContentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isChecked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onTertiary.copy(0.03f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isChecked) Icons.Filled.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (item.isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                        letterSpacing = (-0.3).sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    maxLines = 1
                )

                if (item.price > 0) {
                    Text(
                        text = formatAmountWithCurrency(item.price),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.list_row_edit_button)) },
                        onClick = { onEdit(item); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.list_row_delete_button), color = MaterialTheme.colorScheme.error) },
                        onClick = { showDeleteDialog = true; showMenu = false },
                        leadingIcon = { Icon(painter = painterResource(R.drawable.delete_icon), null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }

        if (!item.isChecked) {
            Divider(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth(0.85f),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_product_title)) },
            text = { Text(stringResource(R.string.dialog_delete_product_text)) },
            confirmButton = {
                TextButton(
                    onClick = { onDelete(item.id); showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.list_row_delete_button), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}