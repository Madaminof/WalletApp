package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptEntity
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptItemEntity
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.ReceiptItem

// --- Receipt Entity -> Domain ---
fun ReceiptEntity.toDomain(items: List<ReceiptItemEntity>): Receipt {
    return Receipt(
        id = this.id,
        originalUrl = originalUrl, // MUHIM: Entity'ga uzatish
        transactionId = this.transactionId,
        merchantName = this.merchantName,
        merchantAddress = this.merchantAddress,
        date = this.date,
        totalAmount = this.totalAmount,
        taxAmount = this.taxAmount,
        paymentMethod = this.paymentMethod,
        fiscalSign = this.fiscalSign,
        rawText = this.rawText,
        items = items.map { it.toDomain() },
    )
}

// --- ReceiptItem Entity -> Domain ---
fun ReceiptItemEntity.toDomain(): ReceiptItem {
    return ReceiptItem(
        id = this.id,
        name = this.productName, // Baza maydoni nomi 'productName' edi
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        totalPrice = this.totalPrice,
        categoryId = this.categoryId
    )
}

// --- Receipt Domain -> Entity ---
fun Receipt.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = this.id,
        originalUrl = originalUrl, // MUHIM: Entity'ga uzatish
        transactionId = this.transactionId,
        merchantName = this.merchantName,
        merchantAddress = this.merchantAddress,
        date = this.date,
        totalAmount = this.totalAmount,
        taxAmount = this.taxAmount,
        paymentMethod = this.paymentMethod,
        fiscalSign = this.fiscalSign,
        rawText = this.rawText,
    )
}

// --- ReceiptItem Domain -> Entity ---
// Bu yerda receiptId parametr sifatida beriladi
fun ReceiptItem.toEntity(receiptId: String): ReceiptItemEntity {
    return ReceiptItemEntity(
        id = this.id,
        receiptId = receiptId, // Domain modelda yo'q, lekin bazaga kerak
        productName = this.name,
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        totalPrice = this.totalPrice,
        categoryId = this.categoryId
    )
}