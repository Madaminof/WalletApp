package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1.bottomShetts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionUiState

@Composable
fun CategorySelectorSheet(
    uiState: AddTransactionUiState,
    selectedTabIndex: Int,
    onCategorySelected: (Category) -> Unit
) {
    val currentGridList = if (selectedTabIndex == 0) uiState.expenseCategories else uiState.incomeCategories

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Kategoriyani tanlang",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 360.dp)
        ) {
            items(currentGridList, key = { it.id ?: it.name }) { category ->
                val isSelected = uiState.selectedCategory?.id == category.id
                ItemCategoryOptimized(
                    category = category,
                    isSelected = isSelected,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun ItemCategoryOptimized(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val baseColor = if (category.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val backgroundColor = if (isSelected) baseColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
    val iconTint = if (isSelected) baseColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
    val textColor = if (isSelected) baseColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (category.iconResId != null) {
                Icon(
                    painter = painterResource(id = category.iconResId),
                    contentDescription = category.name,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Category,
                    contentDescription = category.name,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.name,
            fontSize = 11.sp,
            fontWeight = fontWeight,
            color = textColor,
            maxLines = 1
        )
    }
}
