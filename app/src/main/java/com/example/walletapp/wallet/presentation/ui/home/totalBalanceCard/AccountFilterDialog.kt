package com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Account


@Composable
fun AccountFilterDialog(
    accounts: List<Account>,
    selectedAccountIds: Set<String>,
    onAccountSelectionChange: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 12.dp)),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier.width(40.dp).clip(CircleShape),
                        thickness = 4.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
                Text(
                    text = "Select Accounts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(accounts) { account ->
                        val isChecked = selectedAccountIds.contains(account.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onAccountSelectionChange(account.id, !isChecked) }
                                .background(if (isChecked) primaryAccent.copy(alpha = 0.08f) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(account.colorHex ?: "#AAAAAA")))
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = account.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isChecked) primaryAccent else MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                                )
                            }
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                if (isChecked) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(primaryAccent)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onTertiary.copy(0.05f))
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.onTertiary.copy(0.06f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = selectedAccountIds.isNotEmpty(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                ) {
                    Text(
                        text = "Save (${selectedAccountIds.size}/${accounts.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}