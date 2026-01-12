package dev.samandar.walletapp.wallet.data.repository


import dev.samandar.walletapp.wallet.data.local.dao.ShoppingDao
import dev.samandar.walletapp.wallet.data.mapper.toDomain
import dev.samandar.walletapp.wallet.data.mapper.toEntity
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao
) : ShoppingRepository {

    override fun getAllLists(): Flow<List<ShoppingList>> {
        return dao.getAllLists().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createList(list: ShoppingList) {
        dao.insertList(list.toEntity())
    }

    override suspend fun updateList(list: ShoppingList) {
        dao.updateList(list.toEntity())
    }

    override suspend fun deleteList(listId: String) {
        dao.deleteList(listId)
    }

    override suspend fun getListsByListId(listId: String): ShoppingList? {
        val listEntity = dao.getListById(listId)
        return listEntity?.toDomain()
    }

    override fun getItemsByListId(listId: String): Flow<List<ShoppingItem>> {
        return dao.getItems(listId).map { items ->
            items.map { it.toDomain() }
        }
    }

    override suspend fun addItem(item: ShoppingItem) {
        dao.insertItem(item.toEntity())
    }

    override suspend fun updateItem(item: ShoppingItem) {
        dao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(itemId: String) {
        dao.deleteItem(itemId)
    }
}
