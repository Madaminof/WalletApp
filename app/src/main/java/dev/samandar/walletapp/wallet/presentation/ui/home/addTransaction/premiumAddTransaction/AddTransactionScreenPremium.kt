package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.kalkulator.PremiumCalculatorSheet
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account.AccountSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator.CalculatorPadPremiumUI
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.CategoryListSectionPremium
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime.PremiumDateTimePickerDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.helperFunctions.ZoomDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note.NoteDialogContent
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note.NoteSelectionButton
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.MySnackbarVisuals
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.TransactionEvent

@Composable
fun AddTransactionScreenPremium(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onSuccess: (String) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.uiState

    var showCalculatorSheet by remember { mutableStateOf(false) }

    var showNoteDialog by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var currentDisplayString by remember { mutableStateOf("0") }

    val successMessageSave = stringResource(Strings.snackbar_transaction_saved_success)
    val errorCategory = stringResource(Strings.snackbar_error_select_category)
    val errorAmount = stringResource(Strings.snackbar_error_max_amount_zero)

    var searchQuery by remember { mutableStateOf("") }

    val allCategories = if (state.selectedType == TransactionType.EXPENSE)
        state.expenseCategories else state.incomeCategories

    val filteredCategories = remember(searchQuery, allCategories) {
        if (searchQuery.isEmpty()) {
            allCategories
        } else {
            allCategories.filter {
                // Agar getTranslatedName funksiyang bo'lsa, shuni ishlat
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TransactionEvent.ShowSnackbar -> {

                    val displayMessage = when (event.message) {
                        Strings.snackbar_error_select_category.toString() -> errorCategory
                        Strings.snackbar_error_max_amount_zero.toString() -> errorAmount
                        else -> event.message
                    }

                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        visuals = MySnackbarVisuals(
                            message = displayMessage,
                            isError = true
                        )
                    )
                }

                is TransactionEvent.Success -> {
                    onSuccess(successMessageSave)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AddTransactionHeaderPremium(
                    onClose = onBack,
                    searchQuery = searchQuery, // Joriy matn
                    onSearchQueryChange = { searchQuery = it } // Matn o'zgarganda yangilash
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                TransactionTypeTabRowPremium(
                    selected = state.selectedType,
                    onSelect = viewModel::onTypeChange
                )

                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()) {

                    CategoryListSectionPremium(
                        categories = filteredCategories, // Mana bu yerga filtrlanganini beramiz
                        selected = state.selectedCategory,
                        onSelect = viewModel::onCategorySelect,
                        playCustomSound = { SoundManager.playClick() }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 0.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        NoteSelectionButton(
                            note = state.note,
                            onClick = {
                                SoundManager.playClick()
                                showNoteDialog = true
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // 1. BLUR LAYER (Faqat Android 12+ uchun haqiqiy xiralashish)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer {
                                    // Mana shu joy haqiqiy xiralashishni beradi
                                    renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                        40f, 40f, android.graphics.Shader.TileMode.CLAMP
                                    ).asComposeRenderEffect()
                                }
                                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .premiumShadow(
                                alpha = 0.25f,
                                shadowRadius = 45.dp,
                                offsetY = (-15).dp
                            ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            CalculatorPadPremiumUI(
                                onDisplayChange = { currentDisplayString = it },
                                onSaveConfirmed = { finalAmount ->
                                    viewModel.saveTransaction(finalAmount)
                                },
                                onDateClick = {
                                    SoundManager.playClick()
                                    showDateTimePicker = true
                                },
                                onAmountClick = {
                                    SoundManager.playClick()
                                    showCalculatorSheet = true
                                },
                                selectedDate = state.selectedDate,
                                currentValue = currentDisplayString,
                                selectedAccount = state.selectedAccount,
                                onClick = {
                                    SoundManager.playClick()
                                    showAccountDialog = true
                                }
                            )
                        }
                    }
                }



            }

            if (showNoteDialog) {
                ZoomDialog(onDismiss = { showNoteDialog = false }) { animateOut ->
                    NoteDialogContent(viewModel = viewModel, onDismiss = animateOut)
                }
            }

            if (showDateTimePicker) {
                PremiumDateTimePickerDialog(
                    initialDateTime = state.selectedDate,
                    onConfirm = { newDate ->
                        viewModel.onDateChange(newDate)
                        showDateTimePicker = false
                    },
                    onDismiss = { showDateTimePicker = false }
                )
            }

            if (showAccountDialog) {
                AccountSelectionDialog(
                    accounts = state.accounts,
                    selectedAccountId = state.selectedAccount?.id,
                    onAccountSelect = { account ->
                        viewModel.onAccountSelect(account)
                        showAccountDialog = false
                    },
                    onDismiss = { showAccountDialog = false }
                )
            }

            if (showCalculatorSheet) {
                PremiumCalculatorSheet(
                    initialValue = currentDisplayString,
                    onConfirm = { finalCalculatedAmount ->
                        currentDisplayString = finalCalculatedAmount
                        showCalculatorSheet = false
                    },
                    onDismiss = { showCalculatorSheet = false },
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
                .zIndex(1f),
            snackbar = { data ->
                AnimatedContent(
                    targetState = data,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { -it }) + fadeIn() + scaleIn(
                            initialScale = 0.8f
                        ))
                            .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                            .using(SizeTransform(clip = false))
                    },
                    label = "PremiumSnackbarAnimation"
                ) { targetData ->
                    ModernSnackbar(snackbarData = targetData)
                }
            }
        )
    }
}


fun Modifier.premiumShadow(
    color: Color = Color.Black,
    alpha: Float = 0.15f,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = (-10).dp
) = this.drawBehind {
    val transparentColor = color.copy(alpha = 0f).toArgb()
    val shadowColor = color.copy(alpha = alpha).toArgb()

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        // Soyaga haqiqiy Blur beramiz
        frameworkPaint.color = shadowColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            0f,
            offsetY.toPx(),
            shadowColor
        )

        // Surface shakliga mos soya chizamiz
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = 32.dp.toPx(), // Surface shape bilan bir xil bo'lishi shart
            radiusY = 32.dp.toPx(),
            paint = paint
        )
    }
}