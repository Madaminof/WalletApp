package dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.charts.SortState


@Composable
fun SortSelectionDialog(
    currentSortState: SortState,
    onSortSelected: (SortState) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSortState by remember { mutableStateOf(currentSortState) }

    val dialogBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)

    val sortStateName = when(tempSortState) {
        SortState.DATE_DESC -> stringResource(R.string.sort_state_date_desc)
        SortState.AMOUNT_DESC -> stringResource(R.string.sort_state_amount_desc)
        SortState.AMOUNT_ASC -> stringResource(R.string.sort_state_amount_asc)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 12.dp)),
            color = dialogBackgroundColor,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier
                            .width(40.dp)
                            .clip(CircleShape),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }

                Text(
                    text = stringResource(R.string.sort_dialog_title), // ✅ String resource
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SortOptionItem(
                        label = stringResource(R.string.sort_option_date_desc), // ✅ String resource
                        state = SortState.DATE_DESC,
                        icon = Icons.Default.AccessTime,
                        current = tempSortState,
                        onClick = { tempSortState = it }
                    )
                    SortOptionItem(
                        label = stringResource(R.string.sort_option_amount_desc), // ✅ String resource
                        state = SortState.AMOUNT_DESC,
                        icon = Icons.Default.ArrowUpward,
                        current = tempSortState,
                        onClick = { tempSortState = it }
                    )
                    SortOptionItem(
                        label = stringResource(R.string.sort_option_amount_asc), // ✅ String resource
                        state = SortState.AMOUNT_ASC,
                        icon = Icons.Default.ArrowDownward,
                        current = tempSortState,
                        onClick = { tempSortState = it }
                    )
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        onSortSelected(tempSortState)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = true
                ) {
                    Text(
                        text = stringResource(R.string.sort_button_save, sortStateName),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(
    label: String,
    state: SortState,
    icon: ImageVector,
    current: SortState,
    onClick: (SortState) -> Unit
) {
    val isChecked = current == state
    val primaryAccent = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick(state) }
            .background(if (isChecked) primaryAccent.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.icon_content_desc_date),
            tint = if (isChecked) primaryAccent else textColor.copy(0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isChecked) primaryAccent else textColor,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            if (isChecked) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(R.string.sort_content_desc_selected), // ✅ String resource
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(primaryAccent)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(0.3f), CircleShape)
                )
            }
        }
    }
}