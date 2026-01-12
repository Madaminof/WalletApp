package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager.FormatStyle


@Composable
fun NumberFormatSelectionDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val currentStyle by NumberFormatManager.currentStyle
    var tempSelectedStyle by remember { mutableStateOf(currentStyle) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val titleColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
    val strongTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    val secondaryTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.4f)
    val borderColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Strings.setting_numFormat_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = titleColor,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FormatStyle.values().forEach { style ->
                        val isChecked = tempSelectedStyle == style

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { tempSelectedStyle = style }
                                .background(if (isChecked) primaryColor.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(id = style.titleResId),
                                    fontSize = 16.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = strongTextColor,
                                )
                                Text(
                                    text = "${stringResource(Strings.setting_numFormat_example)}: ${style.example}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = secondaryTextColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(primaryColor)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 2.dp,
                                                color = borderColor,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = tempSelectedStyle != currentStyle,
                    onClick = {
                        NumberFormatManager.saveStyle(context, tempSelectedStyle)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                        disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f)
                    )
                ) {
                    val styleTitle = stringResource(id = tempSelectedStyle.titleResId)

                    Text(
                        text = "${stringResource(R.string.btn_save)} ($styleTitle)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}