package dev.samandar.walletapp.wallet.presentation.ui.drawableMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings

@Composable
fun DrawerHeader() {
    val appVersion = "1.10"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer) // Drawer foni bilan bir xil
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary.copy(0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.2f))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_wallet_2),
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Strings.app_name),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.9f)
        )

        Surface(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary.copy(0.08f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "v$appVersion",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
@Preview
@Composable
private fun DrawerHeaderPreview() {
    DrawerHeader()
}