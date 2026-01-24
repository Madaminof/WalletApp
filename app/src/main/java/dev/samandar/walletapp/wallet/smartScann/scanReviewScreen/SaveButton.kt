package dev.samandar.walletapp.wallet.smartScann.scanReviewScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings


@Composable
fun BottomActionArea(
    isSaving: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onConfirmed: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.92f), // Bir oz shaffof
        tonalElevation = 12.dp // Soya orqali chuqurlik beramiz
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Navigatsiya paneli ustiga tushib qolmasligi uchun
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 2. Premium Tugma
            Button(
                onClick = onConfirmed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp) // Bir oz balandroq, premium qulaylik uchun
                    .clip(RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                enabled = !isSaving && enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                ),
                contentPadding = PaddingValues(0.dp) // Ichki bo'shliqni nollaymiz
            ) {
                AnimatedContent(
                    targetState = isSaving,
                    label = "SaveButtonAnimation"
                ) { saving ->
                    if (saving) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Saqlanmoqda...",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(Strings.btn_save).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}