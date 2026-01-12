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
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings

@Composable
fun DrawerHeader() {
    val appVersion = "1.1.0"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.primary.copy(0.1f),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_wallet_2),
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Strings.app_name),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
        Text(
            text = "Version $appVersion",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f)
        )
    }
}

@Preview
@Composable
private fun DrawerHeaderPreview() {
    DrawerHeader()
}