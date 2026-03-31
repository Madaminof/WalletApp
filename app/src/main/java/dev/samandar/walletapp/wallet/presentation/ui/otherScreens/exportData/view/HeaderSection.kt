package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.Strings


@Composable
fun HeaderSection(onDismiss: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(Strings.export_header_title).uppercase(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
            Text(
                stringResource(Strings.export_header_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.background(MaterialTheme.colorScheme.onTertiary.copy(0.05f), CircleShape)
        ) {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        }
    }
}