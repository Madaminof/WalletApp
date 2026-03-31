package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.factory

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.DataExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.csv.CsvExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.excel.ExcelExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf.PdfExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.reportButton.ExportFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExporterFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun createExporter(format: ExportFormat): DataExporter {
        return when (format) {
            ExportFormat.PDF -> PdfExporter(context)
            ExportFormat.EXCEL -> ExcelExporter(context)
            ExportFormat.CSV -> CsvExporter(context)
        }
    }
}