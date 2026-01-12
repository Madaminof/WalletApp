package dev.samandar.walletapp.wallet.domain.repository

import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {

    fun getAllLists(): Flow<List<ShoppingList>>
    suspend fun createList(list: ShoppingList)
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(listId: String)

    suspend fun getListsByListId(listId: String): ShoppingList?

    fun getItemsByListId(listId: String): Flow<List<ShoppingItem>>
    suspend fun addItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(itemId: String)
}