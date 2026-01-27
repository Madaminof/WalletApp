package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.ui.theme.expenseColor


@Composable
fun ActionButtonsRow(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = onEdit,
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.edit2_icon),
                contentDescription = "Edit",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        FilledIconButton(
            onClick = onDelete,
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
                contentColor =MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.delete_icon),
                contentDescription = "Delete",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

