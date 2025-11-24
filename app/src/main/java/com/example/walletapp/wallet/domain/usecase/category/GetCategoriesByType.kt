package com.example.walletapp.wallet.domain.usecase.category


import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesByType @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: TransactionType): Flow<List<Category>> {
        return categoryRepository.getCategories(type)
    }
}