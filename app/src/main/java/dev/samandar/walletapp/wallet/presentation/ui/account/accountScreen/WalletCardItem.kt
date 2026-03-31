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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmountAccount


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



@Composable
fun WalletCardItem(
    account: Account,
    isDefault: Boolean,
    onClick: (Account) -> Unit,
) {
    val context = LocalContext.current
    val accountName = getTranslatedName(account.name)
    val baseColor = parseColor(account.colorHex)

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val safeIconId = remember(account.iconResId) {
        val id = account.iconResId ?: 0
        if (id > 0) getSafeIconId(context, id) else R.drawable.card_default_icon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = baseColor.copy(alpha = 0.08f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = safeIconId),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = baseColor
                        )
                    }
                }
                val isSystemAccount = account.id == "default_cash" || account.id == "default_card"

                if (isSystemAccount) {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .offset(x = 2.dp, y = (-2).dp)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer, CircleShape)
                            .padding(1.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center // Markazda turishi uchun
            ) {
                Text(
                    text = accountName.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // FAQAT KARTA BO'LSA VA RAQAMI BO'LSA KO'RSATAMIZ
                if (account is Account.Card && !account.cardNumber.isNullOrBlank()) {
                    val number = account.cardNumber.replace(" ", "")
                    val maskedNumber = if (number.length >= 8) {
                        "${number.take(4)} •••• ${number.takeLast(4)}"
                    } else {
                        account.cardNumber
                    }

                    Text(
                        text = maskedNumber,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${FormatAmountAccount(account.amountCurrencyKonverter)} ${getCurrencySymbol(account.currencyCode)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = if (account.amountCurrencyKonverter < 0) expenseColor else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    ),
                    textAlign = TextAlign.End
                )

                /*if (account.currencyCode != "UZS") {
                    Text(
                        text = "≈ ${FormatAmountAccount(account.balance)} so'm",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    )
                }*/
            }
        }
    }
}


fun getCurrencySymbol(code: String): String {
    return when (code) {
        "UZS" -> "so'm"
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> code
    }
}
