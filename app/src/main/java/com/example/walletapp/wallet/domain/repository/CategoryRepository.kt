package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow


interface CategoryRepository {
    fun getCategories(type: TransactionType): Flow<List<Category>>
    suspend fun addCategory(category: Category): Result<Unit>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    suspend fun getCategoryById(categoryId: String): Result<Category>
}