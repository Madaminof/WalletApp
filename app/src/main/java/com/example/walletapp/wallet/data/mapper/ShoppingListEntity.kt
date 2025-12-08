package com.example.walletapp.wallet.data.mapper

import com.example.walletapp.wallet.data.local.entity.ShoppingListEntity
import com.example.walletapp.wallet.data.local.entity.ShoppingItemEntity
import com.example.walletapp.wallet.domain.model.ShoppingList
import com.example.walletapp.wallet.domain.model.ShoppingItem


// ---------- ShoppingList Mapper ----------
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


// ---------- ShoppingItem Mapper ----------
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
