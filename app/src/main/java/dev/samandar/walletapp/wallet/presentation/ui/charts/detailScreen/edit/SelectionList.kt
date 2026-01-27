@file:Suppress("IMPLICIT_CAST_TO_ANY")
package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName

@Composable
fun <T> SelectionList(
    items: List<T>,
    initialSelected: T,
    onDismiss: () -> Unit,
    getName: (T) -> String,
    getIcon: ((T) -> Int?)? = null,
    getColor: ((T) -> Color)? = null,
    onSave: (T) -> Unit
) {
    val sortedItems = remember(items, initialSelected) {
        items.sortedWith(compareByDescending { it == initialSelected })
    }

    var tempSelected by remember { mutableStateOf(initialSelected) }
    val isChanged = tempSelected != initialSelected

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sortedItems) { item ->
                val isSelected = item == tempSelected
                val itemThemeColor = getColor?.invoke(item) ?: MaterialTheme.colorScheme.primary
                Surface(
                    onClick = { tempSelected = item },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) itemThemeColor.copy(alpha = 0.12f) else Color.Transparent,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        getIcon?.invoke(item)?.let { iconRes ->
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = if (isSelected) itemThemeColor else itemThemeColor.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        Text(
                            text = getTranslatedName(getName(item)).toString(),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected)
                                    itemThemeColor
                                else
                                    MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                            ),
                            fontSize = 13.sp
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = itemThemeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DialogActionButtons(
            onDismiss = onDismiss,
            onSave = { onSave(tempSelected) },
            saveEnabled = isChanged
        )
    }
}

