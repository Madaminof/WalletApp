package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import dev.samandar.walletapp.utils.Strings


@Composable
fun BudgetPreviewCard(
    categoryName: String?,
    maxAmount: Double,
    period: BudgetPeriod,
    selectedColor: Color?
) {
    val isDefault = selectedColor == null
    val baseColor = selectedColor ?: MaterialTheme.colorScheme.primaryContainer
    val categoryDisplayName = getTranslatedName(categoryName ?: stringResource(R.string.preview_card_placeholder_category))

    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 1.03f else 1f,
        animationSpec = spring(0.7f, Spring.StiffnessLow), label = ""
    )
    val rotateX by animateFloatAsState(targetValue = if (isPressed) -6f else 0f, label = "")
    val rotateY by animateFloatAsState(targetValue = if (isPressed) 8f else 0f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                rotationX = rotateX
                rotationY = rotateY
                cameraDistance = 15f * density
            }
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                })
            },
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDefault) MaterialTheme.colorScheme.outline.copy(0.2f) else Color.White.copy(0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 12.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isDefault) {
                        Brush.verticalGradient(
                            colors = listOf(
                                baseColor.copy(alpha = 0.5f),
                                baseColor.copy(alpha = 0.8f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(baseColor.copy(alpha = 0.95f), baseColor.darken(0.4f))
                        )
                    }
                )
        ) {
            if (!isDefault) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width * 0.8f, size.height * 0.2f),
                            radius = size.width * 0.6f
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val contentColor = if (isDefault) MaterialTheme.colorScheme.onTertiary else Color.White

                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(
                        text = stringResource(R.string.preview_card_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = contentColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = categoryDisplayName.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        ),
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.align(Alignment.CenterStart).padding(top = 40.dp)) {
                    Text(
                        text = if (maxAmount == 0.0) "—" else FormatAmount(maxAmount),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp,
                            letterSpacing = (-1.5).sp
                        ),
                        color = contentColor
                    )
                    Text(
                        text = stringResource(Strings.preview_card_limit_label).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        color = contentColor.copy(alpha = 0.5f)
                    )
                }

                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = if (isDefault) contentColor.copy(0.1f) else Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = period.toLocalizedString().uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = contentColor.copy(0.8f)
                    )
                }
            }
        }
    }
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}