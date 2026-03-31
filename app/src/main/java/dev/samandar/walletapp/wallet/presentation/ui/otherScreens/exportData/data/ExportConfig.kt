package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data

import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.reportButton.ExportFormat


data class ExportConfig(
    val startDate: Long?,
    val endDate: Long?,
    val categoryId: String? = null,
    val accountId: String? = null,
    val format: ExportFormat,
)


