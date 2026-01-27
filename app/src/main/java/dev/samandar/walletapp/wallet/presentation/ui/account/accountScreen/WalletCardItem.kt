package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount


private fun parseColor(colorHex: String?): Color {
    return try {
        if (colorHex.isNullOrBlank() || !colorHex.startsWith("#")) {
            Color(0xFF8D6E63)
        } else {
            Color(android.graphics.Color.parseColor(colorHex))
        }
    } catch (e: IllegalArgumentException) {
        Color(0xFF8D6E63)
    }
}
private fun isLightColor(color: Color): Boolean {
    val luminance = 0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue
    return luminance > 0.5
}


@Composable
fun WalletCardItem(
    account: Account,
    isDefault: Boolean,
    onClick: (Account) -> Unit
) {
    val accountName = getTranslatedName(account.name)
    val baseColor = parseColor(account.colorHex)
    val isLight = isLightColor(baseColor)

    val contentColor = if (isLight) Color(0xFF1C1C1E) else Color.White
    val secondaryContentColor = contentColor.copy(alpha = 0.6f)
    val isNegative = account.initialBalance < 0
    val warningColor = Color(0xFFFF453A)

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "press_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick(account)
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = baseColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = if (isNegative) BorderStroke(1.dp, warningColor.copy(alpha = 0.7f)) else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(contentColor.copy(alpha = 0.07f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(0.1f), Color.Transparent, Color.Black.copy(0.05f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = contentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(44.dp),
                        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.15f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            account.iconResId?.let {
                                Icon(
                                    painter = painterResource(id = it),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = contentColor
                                )
                            }
                        }
                    }
                    StatusTag(isDefault, contentColor)
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = accountName.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = secondaryContentColor,
                            fontSize = 14.sp,
                            letterSpacing = 0.2.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = FormatAmount(account.initialBalance),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = contentColor,
                            letterSpacing = (-0.5).sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Composable
private fun StatusTag(isDefault: Boolean, contentColor: Color) {
    val icon = if (isDefault) Icons.Rounded.VerifiedUser else Icons.Rounded.Fingerprint
    val label = if (isDefault) stringResource(R.string.account_tag_main) else stringResource(R.string.account_tag_custom)

    Surface(
        color = contentColor.copy(alpha = 0.12f),
        shape = CircleShape,
        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = contentColor.copy(alpha = 0.8f)
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    color = contentColor.copy(alpha = 0.8f)
                )
            )
        }
    }
}