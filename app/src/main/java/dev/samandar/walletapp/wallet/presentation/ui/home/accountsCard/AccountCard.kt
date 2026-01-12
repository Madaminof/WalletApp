package dev.samandar.walletapp.wallet.presentation.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.AccountDetailBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun AccountItem(
    account: Account,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    ) {
    val accountName = getTranslatedName(account.name)

    val amountColor = if (account.initialBalance < 0) expenseColor else MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    val hexColor = try {
        Color(android.graphics.Color.parseColor(account.colorHex ?: "#AAAAAA"))
    } catch (e: IllegalArgumentException) {
        Color.Gray
    }


    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable{onDismiss()}
            .padding(vertical = 2.dp, horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(hexColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = account.iconResId?: R.drawable.ic_card_default),
                contentDescription = account.name,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = accountName.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                style = LocalTextStyle.current.copy(lineHeight = 16.sp)

            )
            Text(
                text = FormatAmount(account.initialBalance),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = amountColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = LocalTextStyle.current.copy(lineHeight = 14.sp)
            )
        }
    }
}


@Composable
fun AccountCardSkeleton() {
    val shimmerAlpha by animateFloatAsState(
        targetValue = 0.4f,
        animationSpec = tween(1000),
        label = "shimmer"
    )
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = shimmerAlpha)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(120.dp).height(20.dp).clip(RoundedCornerShape(4.dp))
                    .background(shimmerColor)
            )
            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        repeat(2) {
                            Row(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(shimmerColor))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Box(modifier = Modifier.width(70.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmerColor))
                                    Spacer(Modifier.height(4.dp))
                                    Box(modifier = Modifier.width(90.dp).height(13.dp).clip(RoundedCornerShape(4.dp)).background(shimmerColor))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AccountCard(
    viewModel: AccountViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.cardState.collectAsState()
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScale"
    )

    if (state.isLoading) {
        AccountCardSkeleton()
        return
    }

    val chunkedAccounts = state.accounts.chunked(2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false })
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Strings.title_account),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = {navController.navigate(Screen.Wallet.route)},
                    icon = Icons.Default.ArrowForwardIos,
                    contentDescription = "Go to accounts list",
                    tint = primaryAccent,
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }
            if (state.accounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hisoblar hali qo'shilmagan.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chunkedAccounts.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { account ->
                                AccountItem(
                                    account = account,
                                    modifier = Modifier.weight(1f),
                                    onDismiss = {selectedAccount = account}
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
    selectedAccount?.let { account ->
        AccountDetailBottomSheet(
            account = account,
            onDismiss = { selectedAccount = null },
            onUpdate = { acc ->
                // TODO: Update logic
                selectedAccount = null
            },
            onDelete = { acc ->
                viewModel.deleteAccount(acc)
                selectedAccount = null
            }
        )
    }
}