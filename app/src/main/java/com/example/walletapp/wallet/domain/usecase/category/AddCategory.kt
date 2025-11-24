package com.example.walletapp.wallet.domain.usecase.category


import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import java.util.UUID
import javax.inject.Inject

class AddCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        // 1. Biznes Qoida: Kategoriya nomi bo'sh bo'lmasligi kerak
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Kategoriya nomi bo'sh bo'lishi mumkin emas."))
        }

        // 2. Yangi ID berish (agar mavjud bo'lmasa)
        val categoryToSave = if (category.id.isBlank()) {
            category.copy(id = UUID.randomUUID().toString())
        } else {
            category
        }

        // 3. Repository orqali saqlash
        return categoryRepository.addCategory(categoryToSave)
    }
}