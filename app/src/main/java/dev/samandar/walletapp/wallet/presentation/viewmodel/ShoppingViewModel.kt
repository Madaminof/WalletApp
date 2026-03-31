package dev.samandar.walletapp.wallet.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.domain.usecase.shopping.ShoppingUseCases
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ListStats(
    val totalItems: Int = 0,
    val totalAmount: Double = 0.0,
    val boughtItems: Int = 0,
)

data class ShoppingListWithStats(
    val list: ShoppingList,
    val stats: ListStats,
    val items: List<ShoppingItem>,
)

data class ShoppingUiState(
    val listsWithStats: List<ShoppingListWithStats> = emptyList(),
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentCurrency: String = "", // Joriy valyuta belgisi uchun
)

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val useCases: ShoppingUseCases,
    private val currencyRepository: CurrencyRepository, // Valyuta kurslari uchun
) : ViewModel() {

    private val _state = MutableStateFlow(ShoppingUiState(isLoading = true))
    val state: StateFlow<ShoppingUiState> = _state

    private val _listName = MutableStateFlow("Yuklanmoqda...")
    val listName: StateFlow<String> = _listName.asStateFlow()

    private var currentListId: String? = null

    // Valyuta va kurslarni kuzatish
    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    fun loadLists() {
        // combine orqali Ro'yxatlar, Valyuta va Kurslar o'zgarganda UI-ni yangilaymiz
        combine(
            useCases.getAllLists(),
            currentCurrencyFlow,
            ratesFlow
        ) { lists, currentCurrency, rates ->

            val listsWithStats = lists.map { list ->
                val items = try {
                    useCases.getItems(list.id).first()
                } catch (e: Exception) {
                    Log.e("ShoppingViewModel", "Itemlarni yuklashda xato: ${e.message}")
                    emptyList<ShoppingItem>()
                }

                // Har bir item narxini konvertatsiya qilamiz
                val convertedItems = items.map { item ->
                    item.copy(price = CurrencyEvaluator.convert(item.price, currentCurrency, rates))
                }

                val totalItems = convertedItems.size
                val boughtItems = convertedItems.count { it.isChecked }
                val totalAmount = convertedItems.sumOf { it.price }

                ShoppingListWithStats(
                    list = list,
                    stats = ListStats(
                        totalItems = totalItems,
                        totalAmount = totalAmount,
                        boughtItems = boughtItems
                    ),
                    items = convertedItems
                )
            }

            _state.value = _state.value.copy(
                listsWithStats = listsWithStats,
                isLoading = false,
                currentCurrency = currentCurrency
            )
        }.launchIn(viewModelScope)
    }

    fun loadItems(listId: String) {
        if (currentListId == listId) return
        currentListId = listId

        _state.value = _state.value.copy(isLoading = true)

        // Itemlarni yuklashda ham valyuta o'zgarishini hisobga olamiz
        combine(
            useCases.getItems(listId),
            currentCurrencyFlow,
            ratesFlow
        ) { items, currentCurrency, rates ->
            val convertedItems = items.map { item ->
                item.copy(price = CurrencyEvaluator.convert(item.price, currentCurrency, rates))
            }

            _state.value = _state.value.copy(
                items = convertedItems,
                isLoading = false,
                currentCurrency = currentCurrency
            )
        }.launchIn(viewModelScope)

        loadListName(listId)
    }

    private fun loadListName(id: String) {
        viewModelScope.launch {
            val list = useCases.getShoppingListByIdUseCase.invoke(id)
            _listName.value = list?.title ?: "Yuklanmoqda..."
        }
    }

    // --- CRUD Funksiyalari (Conversion bilan) ---

    fun addItem(item: ShoppingItem) {
        viewModelScope.launch {
            // Saqlashdan oldin narxni bazaviy valyutaga (UZS) o'giramiz
            val currency = currentCurrencyFlow.first()
            val rates = ratesFlow.first()
            val basePrice = CurrencyEvaluator.convertToBase(item.price, currency, rates)

            useCases.addItem(item.copy(price = basePrice))
        }
    }

    fun updateItem(item: ShoppingItem) {
        viewModelScope.launch {
            // Update qilishda agar narx o'zgargan bo'lsa konvertatsiya kerak
            // Lekin biz har doim kiritilayotgan qiymatni UI-dan kelgan deb hisoblab bazaga o'giramiz
            val currency = currentCurrencyFlow.first()
            val rates = ratesFlow.first()
            val basePrice = CurrencyEvaluator.convertToBase(item.price, currency, rates)

            useCases.updateItem(item.copy(price = basePrice))
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
                useCases.createList(newList)
                onSuccess(newListId)
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Ro'yxat yaratishda xato: ${e.message}")
            }
        }
    }
}