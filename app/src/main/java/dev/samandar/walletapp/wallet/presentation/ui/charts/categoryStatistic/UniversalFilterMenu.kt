package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun FilterActionButton(
    onClick: () -> Unit,
    icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    bgColor: Color = MaterialTheme.colorScheme.primary.copy(0.2f),
    icColor:Color = MaterialTheme.colorScheme.primary.copy(0.7f)
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Filter",
            tint = icColor,
            modifier = Modifier.size(size * 0.65f)
        )
    }
}

@Composable
fun <T> UniversalFilterMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    selectedFilter: T,
    filters: Array<T>,
    getLabel: @Composable (T) -> String,
    onFilterSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        filters.forEach { filter ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = getLabel(filter),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                },
                leadingIcon = {
                    if (selectedFilter == filter) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                onClick = {
                    onFilterSelected(filter)
                    onDismiss()
                }
            )
        }
    }
}