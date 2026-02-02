import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions.TabItem

@Composable
fun CommonTabRow(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 2.dp,
                    color = contentColor
                )
            }
        },
        divider = {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.White.copy(alpha = 0.1f)
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = stringResource(tab.titleResId),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) contentColor else contentColor.copy(alpha = 0.3f),
                        letterSpacing = (-0.2).sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
        }
    }
}