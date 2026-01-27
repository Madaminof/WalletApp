package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.wallet.domain.model.Category


@Composable
fun CategoryListSectionPremium(
    categories: List<Category>,
    selected: Category?,
    onSelect: (Category) -> Unit,
    playCustomSound: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { cat ->
            CategoryItem(
                cat = cat,
                isSelected = cat.id == selected?.id,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    playCustomSound()
                    onSelect(cat)
                }
            )
        }
    }
}


