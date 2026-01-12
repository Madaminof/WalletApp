package dev.samandar.walletapp.wallet.domain.usecase.shopping

data class ShoppingUseCases(
    val getAllLists: GetAllListsUseCase,
    val createList: CreateListUseCase,
    val updateList: UpdateListUseCase,
    val deleteList: DeleteListUseCase,
    val getItems: GetItemsUseCase,
    val addItem: AddItemUseCase,
    val updateItem: UpdateItemUseCase,
    val deleteItem: DeleteItemUseCase,
    val getShoppingListByIdUseCase: GetShoppingListByIdUseCase
)
