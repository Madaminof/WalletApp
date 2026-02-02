package dev.samandar.walletapp.wallet.presentation.ui.home.budgetCard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.budjets
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.budjets.PremiumCustomLinearProgressIndicator
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.NumFormat
import dev.samandar.walletapp.wallet.presentation.utils.getCurrencySymbol
import kotlin.math.min
val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun BudgetCard(
    onCardClick: () -> Unit,
    hasActiveBudget: Boolean,
    budgetLimit: Double = 0.0,
    spentAmount: Double = 0.0,
    navController: NavController
) {
    val budgetPrimary = budjets.copy(0.15f)
    val budgetSecondary = budjets.copy(0.15f)

    val progress = if (budgetLimit > 0) min((spentAmount / budgetLimit).toFloat(), 1.0f) else 0.0f

    val progressColor = if (progress >= 0.9) {
        Color(0xFFE57373)
    } else {
        Color(0xFF81C784)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Strings.title_budgets),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = onCardClick,
                    icon = R.drawable.arrow_right_ic,
                    contentDescription = "Go to budgets list",
                    tint = primaryAccent.copy(0.8f),
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }
            Text(
                text = if (hasActiveBudget) {
                    stringResource(Strings.budget_card_txt1)
                } else {
                    stringResource(Strings.budget_card_txt2)
                },
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(budgetPrimary, budgetSecondary)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.budget_ic),
                        contentDescription = "Budget Icon",
                        tint = budjets,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (hasActiveBudget) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${stringResource(Strings.budget_card_sarf)}: ${NumFormat(spentAmount)} / ${NumFormat(budgetLimit)} ${getCurrencySymbol(
                                activeCurrency)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PremiumCustomLinearProgressIndicator(
                            progressFloat = progress,
                            progressColor = progressColor,
                            trackColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Strings.title_add_budget),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = budjets.copy(0.8f),
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Screen.budjetAdd.route)
                            }
                    )
                }
            }
        }
    }
}