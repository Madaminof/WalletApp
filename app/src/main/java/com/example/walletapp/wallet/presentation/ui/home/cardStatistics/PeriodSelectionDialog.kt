package com.example.walletapp.wallet.presentation.ui.home.cardStatistics


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent


@Composable
fun PeriodSelectionDialog(
    selectedPeriod: TimePeriod,
    onDismiss: () -> Unit,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    var tempSelectedPeriod by remember { mutableStateOf(selectedPeriod) }

    val dialogBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 12.dp)),
            color = dialogBackgroundColor,
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
                        modifier = Modifier
                            .width(40.dp)
                            .clip(CircleShape),
                        thickness = 4.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
                Text(
                    text = "Vaqt oralig'ini tanlash",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ALL_PERIODS.forEach { period ->
                        val isSelected = period == tempSelectedPeriod

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { tempSelectedPeriod = period }
                                .background(if (isSelected) primaryAccent.copy(alpha = 0.08f) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = period.name,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) primaryAccent else textColor
                            )

                            // 🚨 Checkbox Uslubi (CashFlowFilterDialog dan olindi)
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                if (isSelected) {
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
                                            .background(MaterialTheme.colorScheme.onTertiary.copy(0.3f))
                                            .border(1.5.dp, Color.LightGray.copy(alpha = 0.7f), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        onPeriodSelected(tempSelectedPeriod)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                    enabled = true
                ) {
                    Text(
                        text = "Qo'llash (${tempSelectedPeriod.name})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}