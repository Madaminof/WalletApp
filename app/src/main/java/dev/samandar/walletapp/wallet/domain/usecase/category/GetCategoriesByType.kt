package dev.samandar.walletapp.wallet.domain.usecase.category


import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesByType @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: TransactionType): Flow<List<Category>> {
        return categoryRepository.getCategories(type)
    }
}