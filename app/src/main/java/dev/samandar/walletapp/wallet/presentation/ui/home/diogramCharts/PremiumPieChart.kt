package dev.samandar.walletapp.wallet.presentation.ui.home.diogramCharts

import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData


@Composable
fun PremiumPieChart(
    data: List<CategoryData>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty() || totalAmount <= 0.0) return

    val sortedList = remember(data) { data.sortedByDescending { it.amount } }

    var selectedCategory by remember { mutableStateOf<CategoryData?>(null) }

    LaunchedEffect(data) { selectedCategory = null }

    val processedEntries = sortedList.map { item ->
        val percentage = (item.amount / totalAmount) * 100
        val translatedName = getTranslatedName(item.categoryName).toString()
        val label = if (percentage > 1.0) "$translatedName\n%.1f%%".format(percentage) else ""

        PieEntry(item.amount.toFloat(), label).apply {
            this.data = item
        }
    }

    val labelColor = MaterialTheme.colorScheme.onTertiary.copy(0.6f).toArgb()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PieChart(ctx).apply {
                        description.isEnabled = false
                        isRotationEnabled = true
                        isDrawHoleEnabled = false
                        legend.isEnabled = false

                        // BIR MARTA BOSGANDA ISHLASHINI TA'MINLASH
                        setTouchEnabled(true)
                        setHighlightPerTapEnabled(true)

                        // Animatsiya vaqtini biroz kamaytiramiz (tezroq javob berishi uchun)
                        setExtraOffsets(50f, 0f, 50f, 0f)

                        setOnChartValueSelectedListener(object : com.github.mikephil.charting.listener.OnChartValueSelectedListener {
                            override fun onValueSelected(e: com.github.mikephil.charting.data.Entry?, h: com.github.mikephil.charting.highlight.Highlight?) {
                                // Entry ichidagi ma'lumotni darhol olyapmiz
                                selectedCategory = e?.data as? CategoryData
                            }
                            override fun onNothingSelected() { selectedCategory = null }
                        })

                        setEntryLabelColor(labelColor)
                        setEntryLabelTextSize(10f)
                        setEntryLabelTypeface(Typeface.DEFAULT_BOLD)

                        // Factory ichida faqat bir marta animatsiya beramiz
                        animateY(800, Easing.EaseOutQuart)
                    }
                },
                update = { chart ->
                    val ds = PieDataSet(processedEntries, "").apply {
                        colors = sortedList.map { it.color.toArgb() }
                        sliceSpace = 2f

                        // Kattalashish effekti (Selection Shift)
                        selectionShift = 15f // Bo'lak sezilarli chiqishi uchun

                        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        valueLinePart1Length = 0.7f
                        valueLinePart2Length = 0.6f
                        valueLineColor = labelColor
                        isUsingSliceColorAsValueLineColor = true
                        setDrawValues(false)
                    }
                    chart.legend.isEnabled = false
                    chart.setExtraOffsets(45f, 0f, 45f, 0f)

                    if (chart.data?.dataSetCount != 1 || chart.data?.entryCount != processedEntries.size) {
                        chart.data = PieData(ds)
                        chart.highlightValues(null)
                        chart.invalidate()
                    }
                }
            )

            Box(
                modifier = Modifier.fillMaxSize().padding(top = 4.dp, end = 10.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                this@Column.AnimatedVisibility(
                    visible = selectedCategory != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f)
                ) {
                    selectedCategory?.let { CompactSelectionCard(it, totalAmount) }
                }
            }
        }
    }
}
@Composable
fun CompactSelectionCard(category: CategoryData, total: Double) {
    val percentage = (category.amount / total * 100)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, category.color.copy(alpha = 0.4f)),
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(category.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconResId ?: R.drawable.card_default_icon),
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = getTranslatedName(category.categoryName).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                Text(
                    text = formatAmountWithCurrency(category.amount),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            Text(
                text = "%.0f%%".format(percentage),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = category.color
            )
        }
    }
}
