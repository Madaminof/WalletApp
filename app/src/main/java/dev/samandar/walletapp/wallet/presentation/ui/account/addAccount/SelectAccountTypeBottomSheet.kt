package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAccountTypeBottomSheet(
    selectedType: AccountType? = null,
    onTypeSelected: (AccountType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(0.5f)) },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.title_select_account_type), // 👈
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            AccountTypeItem(
                title = stringResource(R.string.type_card_title), // 👈
                subtitle = stringResource(R.string.type_card_subtitle), // 👈
                icon = R.drawable.card_default_icon,
                isSelected = selectedType == AccountType.CARD,
                onClick = { onTypeSelected(AccountType.CARD) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccountTypeItem(
                title = stringResource(R.string.type_cash_title), // 👈
                subtitle = stringResource(R.string.type_cash_subtitle), // 👈
                icon = R.drawable.cash_ic1,
                isSelected = selectedType == AccountType.CASH,
                onClick = { onTypeSelected(AccountType.CASH) }
            )
        }
    }
}

@Composable
private fun AccountTypeItem(
    title: String,
    subtitle: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) primaryColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.onTertiary.copy(0.03f),
        border = if (isSelected) BorderStroke(1.5.dp, primaryColor) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}