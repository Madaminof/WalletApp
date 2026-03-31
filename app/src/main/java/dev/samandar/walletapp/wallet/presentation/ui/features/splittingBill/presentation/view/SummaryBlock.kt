package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.BillSummary
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.stringResource
import dev.samandar.walletapp.utils.Strings

class ReceiptShape(
    private val zigzagHeight: Float = 15f,
    private val toothWidth: Float = 25f // Har bir tishning kengligi
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // 1. Tepadan boshlaymiz
            moveTo(0f, 0f)
            lineTo(size.width, 0f)

            // 2. O'ng tomonni chizamiz (pastki zigzag boshlanishigacha)
            lineTo(size.width, size.height - zigzagHeight)

            // 3. Zigzag qismini hisoblaymiz
            val toothCount = (size.width / toothWidth).toInt()
            val actualToothWidth = size.width / toothCount // Bo'shliq qolmasligi uchun aniq kenglik

            for (i in 0 until toothCount) {
                val currentX = size.width - (i * actualToothWidth)
                // Tishning o'rtasi (pastki nuqta)
                lineTo(currentX - (actualToothWidth / 2f), size.height)
                // Tishning oxiri (tepa nuqta)
                lineTo(currentX - actualToothWidth, size.height - zigzagHeight)
            }

            // 4. Chap tomonni tepaga yopamiz
            lineTo(0f, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}
@Composable
fun SummaryBlock(summary: BillSummary) {

    val totalService = summary.individualResults.sumOf { it.serviceCharge }
    val totalTax = summary.individualResults.sumOf { it.tax }
    val totalDiscount = summary.individualResults.sumOf { it.discount }
    val subTotal = summary.individualResults.sumOf { it.itemsSum }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = ReceiptShape(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 40.dp)
        ) {
            // Sarlavha
            Text(
                text = stringResource(Strings.invoice_title),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashedDivider()

            Spacer(modifier = Modifier.height(16.dp))

            summary.individualResults.forEach { res ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = res.participantName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = String.format("%,.0f so'm", res.totalToPay),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            if (totalDiscount > 0 || totalService > 0 || totalTax > 0){
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ReceiptRow(label = stringResource(Strings.subtotal), value = subTotal)

                    if (totalService > 0) {
                        ReceiptRow(
                            label = stringResource(Strings.service_fee),
                            value = totalService,
                            percent = summary.serviceChargePercent // ViewModel/Summary dan kelgan foiz
                        )
                    }

                    if (totalTax > 0) {
                        ReceiptRow(
                            label = stringResource(Strings.tax_fee),
                            value = totalTax,
                            percent = summary.taxPercent // ViewModel/Summary dan kelgan foiz
                        )
                    }

                    if (totalDiscount > 0) {
                        ReceiptRow(
                            label = stringResource(Strings.discount_fee),
                            value = -totalDiscount,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }



            // Jami summa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Strings.total_payment),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = String.format("%,.0f so'm", summary.totalAmount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Strings.footer_text),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DashedDivider() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)) {
        drawLine(
            color = Color.Gray.copy(0.5f),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}


@Composable
fun ReceiptRow(
    label: String,
    value: Double,
    percent: Double? = null, // Yangi ixtiyoriy parametr
    color: Color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = color)
            if (percent != null && percent > 0) {
                Text(
                    text = " (${percent.toInt()}%)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = color
                    )
                )
            }
        }
        Text(
            text = String.format("%,.0f so'm", value),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = color
        )
    }
}