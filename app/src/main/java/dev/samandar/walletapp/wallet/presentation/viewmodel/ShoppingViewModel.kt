package dev.samandar.walletapp.wallet.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem // Item modelining mavjudligini ta'minlang
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.domain.usecase.shopping.ShoppingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ListStats(
    val totalItems: Int = 0,
    val totalAmount: Double = 0.0,
    val boughtItems: Int = 0
)
data class ShoppingListWithStats(
    val list: ShoppingList,
    val stats: ListStats,
    val items: List<ShoppingItem>
)
data class ShoppingUiState(
    val listsWithStats: List<ShoppingListWithStats> = emptyList(),
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val useCases: ShoppingUseCases,

) : ViewModel() {

    private val _state = MutableStateFlow(ShoppingUiState(isLoading = true))
    val state: StateFlow<ShoppingUiState> = _state

    private val _listName = MutableStateFlow("Yuklanmoqda...")
    val listName: StateFlow<String> = _listName.asStateFlow()

    private var currentListId: String? = null

    fun loadLists() {
        useCases.getAllLists().onEach { lists ->
            val listsWithStats = lists.map { list ->
                val items = try {
                    useCases.getItems(list.id).first()
                } catch (e: Exception) {
                    Log.e("ShoppingViewModel", "Itemlarni yuklashda xato: ${e.message}")
                    emptyList<ShoppingItem>()
                }
                val totalItems = items.size
                val boughtItems = items.count { it.isChecked }

                val totalAmount = items.sumOf { it.price }

                ShoppingListWithStats(
                    list = list,
                    stats = ListStats(
                        totalItems = totalItems,
                        totalAmount = totalAmount,
                        boughtItems = boughtItems
                    ),
                    items = items
                )
            }

            _state.value = _state.value.copy(
                listsWithStats = listsWithStats,
                isLoading = false
            )

        }.launchIn(viewModelScope)
    }

    private fun loadListName(id: String) {
        viewModelScope.launch {
            val list = useCases.getShoppingListByIdUseCase.invoke(id)
            _listName.value = list?.title?:"Yuklanmoqda..."
        }
    }

    fun createList(list: ShoppingList) {
        viewModelScope.launch { useCases.createList(list) }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch { useCases.deleteList(listId) }
    }
    fun updateList(list: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                useCases.updateList(list)
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Ro'yxatni yangilashda xato: ${e.message}")
            }
        }
    }

    fun loadItems(listId: String) {
        if (currentListId == listId) return
        currentListId = listId

        _state.value = _state.value.copy(isLoading = true)

        useCases.getItems(listId).onEach { items ->
            _state.value = _state.value.copy(
                items = items,
                isLoading = false
            )
        }.launchIn(viewModelScope)

        loadListName(listId)
    }
    fun addItem(item: ShoppingItem) {
        viewModelScope.launch { useCases.addItem(item) }
    }
    fun updateItem(item: ShoppingItem) {
        viewModelScope.launch { useCases.updateItem(item) }
    }
    fun deleteItem(itemId: String) {
        viewModelScope.launch { useCases.deleteItem(itemId) }
    }

    fun createList(title: String, onSuccess: (String) -> Unit) {
        val newListId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        val newList = ShoppingList(
            id = newListId,
            title = title,
            createdAt = currentTime
        )

        viewModelScope.launch {
            try {
                // repository.insertList emas, useCases ichidagi createList dan foydalanamiz
                useCases.createList(newList)

                // Muvaffaqiyatli bo'lsa, IDni qaytaramiz (Navigatsiya uchun)
                onSuccess(newListId)
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Ro'yxat yaratishda xato: ${e.message}")
            }
        }
    }
}