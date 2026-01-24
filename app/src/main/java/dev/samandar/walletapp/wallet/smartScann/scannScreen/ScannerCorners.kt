package dev.samandar.walletapp.wallet.smartScann.scannScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp


@Composable
fun ScannerCorners(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 4.dp.toPx()
        val cornerSize = 40.dp.toPx()

        // Chap yuqori
        drawScopeLine(Offset(0f, 0f), Offset(cornerSize, 0f), color, strokeWidth)
        drawScopeLine(Offset(0f, 0f), Offset(0f, cornerSize), color, strokeWidth)

        // O'ng yuqori
        drawScopeLine(Offset(size.width, 0f), Offset(size.width - cornerSize, 0f), color, strokeWidth)
        drawScopeLine(Offset(size.width, 0f), Offset(size.width, cornerSize), color, strokeWidth)

        // Chap pastki
        drawScopeLine(Offset(0f, size.height), Offset(cornerSize, size.height), color, strokeWidth)
        drawScopeLine(Offset(0f, size.height), Offset(0f, size.height - cornerSize), color, strokeWidth)

        // O'ng pastki
        drawScopeLine(Offset(size.width, size.height), Offset(size.width - cornerSize, size.height), color, strokeWidth)
        drawScopeLine(Offset(size.width, size.height), Offset(size.width, size.height - cornerSize), color, strokeWidth)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScopeLine(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}