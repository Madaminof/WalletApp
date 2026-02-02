package dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import dev.samandar.walletapp.utils.Strings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    containerColor:Color = MaterialTheme.colorScheme.primaryContainer,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        navigationIcon = {
            PremiumButton(
                onClick = onBackClick,
                icon = R.drawable.back_ic,
                color = MaterialTheme.colorScheme.primary.copy(0.8f),
                modifier = Modifier
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCustomTopBar(
    title: String,
    focusRequester: FocusRequester,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    isSearchMode: Boolean = false, // Qidiruv rejimi yoqilganmi?
    searchQuery: String = "",      // Qidiruv matni
    onSearchQueryChange: (String) -> Unit = {}, // Matn o'zgarganda
    onSearchClose: () -> Unit = {}, // Qidiruvni yopish (X bosilganda)
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            if (isSearchMode) {
                // QIDIRUV REJIMI
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(end = 8.dp),
                    placeholder = {
                        Text(
                            stringResource(Strings.search_placeholder),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.9f)
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.9f)
                )
            }
        },
        navigationIcon = {
            if (isSearchMode) {
                PremiumButton(
                    onClick = onSearchClose,
                    icon = R.drawable.back_ic,
                    color = MaterialTheme.colorScheme.primary.copy(0.8f),
                    modifier = Modifier
                )
            } else {
                PremiumButton(
                    onClick = onBackClick,
                    icon = R.drawable.back_ic,
                    color = MaterialTheme.colorScheme.primary.copy(0.8f),
                    modifier = Modifier
                )
            }
        },
        actions = {
            if (!isSearchMode) {
                actions()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}


@Composable
fun PremiumButton(
    onClick: () -> Unit,
    icon: Int,
    color: Color,
    modifier: Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "scaleAnim"
    )

    Icon(
        painter = painterResource(id = icon),
        contentDescription = "Back",
        tint = color,
        modifier = modifier
            .padding(start = 12.dp)
            .size(32.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > 500L) {
                            lastClickTime = currentTime
                            onClick()
                        }
                    }
                )
            }
    )
}