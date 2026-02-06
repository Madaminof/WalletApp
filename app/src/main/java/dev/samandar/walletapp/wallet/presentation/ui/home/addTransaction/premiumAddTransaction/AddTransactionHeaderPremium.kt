package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.utils.Strings

@Composable
fun AddTransactionHeaderPremium(
    onClose: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var isSearchMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Chap tomondagi tugma
        IconButton(onClick = {
            if (isSearchMode) {
                isSearchMode = false
                onSearchQueryChange("")
            } else {
                onClose()
            }
        }) {
            Icon(
                painter = painterResource(if (isSearchMode) R.drawable.back_ic else R.drawable.close_ic),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(0.8f),
                modifier = Modifier.size(32.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = !isSearchMode,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { -it / 2 }
            ) {
                Text(
                    text = stringResource(R.string.title_add_transaction),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                    textAlign = TextAlign.Center
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchMode,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.search_placeholder),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    singleLine = true
                )
                LaunchedEffect(isSearchMode) {
                    if (isSearchMode) focusRequester.requestFocus()
                }
            }
        }

        IconButton(onClick = {
            if (isSearchMode) {
                onSearchQueryChange("") // Faqat tozalash
            } else {
                isSearchMode = true // Qidiruvni yoqish
            }
        }) {
            AnimatedContent(
                targetState = isSearchMode,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = "HeaderActionTransition"
            ) { searching ->
                if (searching) {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.clear_icon),
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.search_ic),
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}