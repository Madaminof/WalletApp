package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType


@Composable
fun SegmentedTypeSelector(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
) {
    val types = listOf(null, TransactionType.EXPENSE, TransactionType.INCOME)

    val containerColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
    val activeColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            types.forEach { type ->
                val isSelected = selectedType == type
                val label = when (type) {
                    TransactionType.INCOME -> stringResource(Strings.type_income)
                    TransactionType.EXPENSE -> stringResource(Strings.type_expense)
                    else -> stringResource(Strings.type_all)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(
                                        4.dp,
                                        RoundedCornerShape(16.dp),
                                        ambientColor = Color.Black.copy(0.4f)
                                    )
                                    .background(activeColor, RoundedCornerShape(16.dp))
                            } else {
                                Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onTypeSelected(type) }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Gray
                    )
                }
            }
        }
    }
}