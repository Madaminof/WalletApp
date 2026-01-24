package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
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
    val contentColor = if (isLight) Color(0xFF121212) else Color.White
    val isNegative = account.initialBalance < 0

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "press_scale"
    )
    val amountColor = contentColor.copy(alpha = 0.8f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
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
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = baseColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 15.dp, y = 15.dp)
                    .background(contentColor.copy(alpha = 0.05f), CircleShape)
            )
            if (isNegative) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                        .background(expenseColor.copy(alpha = 0.8f))
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = contentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            account.iconResId?.let {
                                Icon(
                                    painter = painterResource(id = it),
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }

                    StatusTag(isDefault, contentColor)
                }

                Column {
                    Text(
                        text = accountName.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = contentColor.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = FormatAmount(account.initialBalance),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = amountColor.copy(0.8f)
                        )
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

    val badgeBgColor = if (isDefault) contentColor.copy(alpha = 0.15f) else contentColor.copy(alpha = 0.08f)

    Row(
        modifier = Modifier
            .background(badgeBgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
            tint = contentColor.copy(alpha = if (isDefault) 0.9f else 0.6f)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = contentColor.copy(alpha = if (isDefault) 0.9f else 0.6f)
            )
        )
    }
}