package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.ShoppingListEntity
import dev.samandar.walletapp.wallet.data.local.entity.ShoppingItemEntity
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem

fun ShoppingListEntity.toDomain(): ShoppingList {
    return ShoppingList(
        id = id,
        title = title,
        createdAt = createdAt
    )
}

fun ShoppingList.toEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        id = id,
        title = title,
        createdAt = createdAt
    )
}

fun ShoppingItemEntity.toDomain(): ShoppingItem {
    return ShoppingItem(
        id = id,
        listId = listId,
        name = name,
        price = price,
        isChecked = isChecked
    )
}

fun ShoppingItem.toEntity(): ShoppingItemEntity {
    return ShoppingItemEntity(
        id = id,
        listId = listId,
        name = name,
        price = price,
        isChecked = isChecked
    )
}
