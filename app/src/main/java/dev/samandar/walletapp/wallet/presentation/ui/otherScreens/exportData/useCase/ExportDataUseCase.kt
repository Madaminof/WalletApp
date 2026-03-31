package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.useCase

import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.factory.ExporterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val accountRepo: dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository,
    private val exporterFactory: ExporterFactory
) {
    suspend fun execute(config: ExportConfig): Result<File> = withContext(Dispatchers.IO) {
        try {
            val transactions = transactionRepo.getFilteredTransactions(config)

            if (transactions.isEmpty()) {
                return@withContext Result.failure(Exception("Eksport qilish uchun tranzaksiyalar topilmadi"))
            }

            val allCategories = combine(
                categoryRepo.getCategories(TransactionType.INCOME),
                categoryRepo.getCategories(TransactionType.EXPENSE)
            ) { income, expense ->
                income + expense
            }.take(1).first()
            val accounts = accountRepo.getAllAccounts().first()

            val exporter = exporterFactory.createExporter(config.format)

            // 3. Eksport qilish
            val file = exporter.export(
                data = transactions,
                categories = allCategories,
                accounts = accounts,
                config = config
            )

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}