package com.example.walletapp.wallet.domain.usecase.category


import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import java.util.UUID
import javax.inject.Inject

class AddCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Kategoriya nomi bo'sh bo'lishi mumkin emas."))
        }
        val categoryToSave = if (category.id.isBlank()) {
            category.copy(id = UUID.randomUUID().toString())
        } else {
            category
        }
        return categoryRepository.addCategory(categoryToSave)
    }
}