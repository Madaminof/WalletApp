package dev.samandar.walletapp.core.onBoarding.onboardingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.core.onBoarding.OnboardingPage

@Composable
fun OnboardingContent(page: OnboardingPage) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(32.dp))
                .shadow(
                    elevation = if (isDark) 0.dp else 12.dp,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
            contentScale = ContentScale.Fit,
            colorFilter = if (isDark) {
                ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    blendMode = BlendMode.Darken
                )
            } else null
        )

        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineMedium.copy(
                lineHeight = 36.sp,
                letterSpacing = (-1).sp
            ),
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(page.descRes),
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 26.sp,
                letterSpacing = 0.1.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}