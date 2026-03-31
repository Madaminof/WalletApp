package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.utils.Strings

@Composable
fun NoteSelectionButton(
    note: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noteShape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 14.dp,
        bottomStart = 14.dp // Mana bu burchak vizual urg'u beradi
    )
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = noteShape,
        color = defaultColor.copy(0.05f),
        border = BorderStroke(
            width = 1.dp,
            color = defaultColor.copy(0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = note.ifEmpty { stringResource(Strings.add_note_label) },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (note.isEmpty())
                    MaterialTheme.colorScheme.onTertiary.copy(0.4f)
                else MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}