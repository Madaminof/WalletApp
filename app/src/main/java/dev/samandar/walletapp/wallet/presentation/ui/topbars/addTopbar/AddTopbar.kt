package dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopBar(
    navController: NavController,
    canSave: Boolean,
    onSave: () -> Unit,
    title:String
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f)),
                contentAlignment = Alignment.Center
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f)),
                contentAlignment = Alignment.Center
            ){
                IconButton(
                    onClick = onSave,
                    enabled = true
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = stringResource(R.string.action_save),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = if (canSave) 1f else 0.4f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}