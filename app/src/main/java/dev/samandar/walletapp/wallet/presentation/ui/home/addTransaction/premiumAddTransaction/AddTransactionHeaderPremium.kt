package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings

@Composable
fun AddTransactionHeaderPremium(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.close_ic),
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.primary.copy(0.8f)
            )
        }
        Box(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
                Text(
                    text = stringResource(R.string.title_add_transaction),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    fontSize = 20.sp
                )
        }
        Spacer(modifier = Modifier.size(48.dp))
    }
}