package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteDetailItem(
    label: String,
    note: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val displayNote = if (note.isNullOrBlank()) "- - - - -" else note
    val isPlaceholder = note.isNullOrBlank()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            modifier = Modifier.weight(2.5f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayNote,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isPlaceholder) MaterialTheme.colorScheme.onTertiary.copy(0.9f) else MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.End,
                softWrap = true,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Edit note",
                tint = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp)
            )
        }
    }
}