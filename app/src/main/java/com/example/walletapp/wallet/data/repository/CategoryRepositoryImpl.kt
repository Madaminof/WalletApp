package com.example.walletapp.wallet.data.repository

import com.example.walletapp.wallet.data.local.dao.CategoryDao
import com.example.walletapp.wallet.data.mapper.toCategory
import com.example.walletapp.wallet.data.mapper.toCategoryEntity
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategories(type: TransactionType): Flow<List<Category>> {
        val typeString = type.name
        return categoryDao.getCategoriesByType(typeString).map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override suspend fun addCategory(category: Category): Result<Unit> = runCatching {
        val entity = category.toCategoryEntity()
        categoryDao.insertCategory(entity)
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        categoryDao.deleteCategoryById(categoryId)
    }

    override suspend fun getCategoryById(categoryId: String): Result<Category> = runCatching {
        val entity = categoryDao.getCategoryEntityById(categoryId)
            ?: throw IOException("Kategoriya topilmadi: ID = $categoryId")

        entity.toCategory()
    }
}