import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.wallet.presentation.ui.charts.formatAmount
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor

@Composable
fun DebtsSummaryCard(totalLent: Double, totalOwed: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Umumiy Qarz Holati",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = "Berilgan Qarz (Sizga)",
                    amount = totalLent,
                    color = incomeColor,
                    icon = Icons.Default.ArrowDownward,
                    isLent = true
                )
                Divider(
                    Modifier
                        .height(80.dp)
                        .width(1.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
                )
                SummaryItem(
                    title = "Olingan Qarz (Sizdan)",
                    amount = totalOwed,
                    color = expenseColor,
                    icon = Icons.Default.ArrowUpward,
                    isLent = false
                )
            }
        }
    }
}

@Composable
fun RowScope.SummaryItem(title: String, amount: Double, color: Color, icon: ImageVector, isLent: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = formatAmount(amount),
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            title,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp

        )
    }
}