package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType

@Composable
fun DebtDetailHeader(
    debt: Debt,
    accentColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(20.dp),
                color = accentColor.copy(alpha = 0.08f)
            ) {}

            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = debt.personName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        ),
                        color = accentColor,
                    )
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        // 2. Ism va Qarz turi
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = debt.personName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onTertiary
            )

            Surface(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = (if(debt.type == DebtType.LENT) stringResource(R.string.debt_status_lent)
                    else stringResource(R.string.debt_status_borrowed)).uppercase(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tahrirlash tugmasi
            DetailActionButton(
                icon = R.drawable.edit2_icon,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiary,
                onClick = onEdit
            )

            // O'chirish tugmasi
            DetailActionButton(
                icon = R.drawable.delete_icon,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.error,
                onClick = onDelete
            )
        }
    }
}

@Composable
fun DetailActionButton(
    icon: Int,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
        }
    }
}