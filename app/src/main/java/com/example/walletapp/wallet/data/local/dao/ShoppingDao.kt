package com.example.walletapp.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.walletapp.wallet.data.local.entity.ShoppingItemEntity
import com.example.walletapp.wallet.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ShoppingDao {

    // Lists
    @Query("SELECT * FROM shopping_lists ORDER BY createdAt DESC")
    fun getAllLists(): Flow<List<ShoppingListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: ShoppingListEntity)

    @Update
    suspend fun updateList(entity: ShoppingListEntity)

    @Query("DELETE FROM shopping_lists WHERE id = :listId")
    suspend fun deleteList(listId: String)

    @Query("SELECT * FROM shopping_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: String): ShoppingListEntity?

    // Items
    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY name ASC")
    fun getItems(listId: String): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(entity: ShoppingItemEntity)

    @Update
    suspend fun updateItem(entity: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: String)
}