package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings

@Composable
fun NoteSelectionButton(
    note: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.9f),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.2f)
        ),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .blur(radius = 8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = note.ifEmpty { stringResource(Strings.add_note_label) },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
            )
        }
    }
}