package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.ShoppingItem
import com.example.walletapp.wallet.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {

    // Shopping Lists
    fun getAllLists(): Flow<List<ShoppingList>>
    suspend fun createList(list: ShoppingList)
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(listId: String)

    suspend fun getListsByListId(listId: String): ShoppingList?

    // Items
    fun getItemsByListId(listId: String): Flow<List<ShoppingItem>>
    suspend fun addItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(itemId: String)
}