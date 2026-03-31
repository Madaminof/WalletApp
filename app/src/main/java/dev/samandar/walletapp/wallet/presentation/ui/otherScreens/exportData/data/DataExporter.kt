package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data

import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import java.io.File


interface DataExporter {
    fun export(
        data: List<TransactionEntity>,
        categories: List<Category>,
        accounts: List<Account>,
        config: ExportConfig,
    ): File
}