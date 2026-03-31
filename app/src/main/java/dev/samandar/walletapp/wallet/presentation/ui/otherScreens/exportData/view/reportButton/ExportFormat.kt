package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.reportButton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.ui.graphics.vector.ImageVector
import dev.samandar.walletapp.utils.Strings

enum class ExportFormat(
    val label: String,
    val descriptionRes: Int,
    val icon: ImageVector
) {
    PDF(
        label = "PDF",
        descriptionRes = Strings.export_pdf_desc,
        icon = Icons.Rounded.PictureAsPdf
    ),
    EXCEL(
        label = "Excel",
        descriptionRes = Strings.export_excel_desc,
        icon = Icons.Rounded.TableChart
    ),
    CSV(
        label = "CSV",
        descriptionRes = Strings.export_csv_desc,
        icon = Icons.Rounded.Description
    )
}