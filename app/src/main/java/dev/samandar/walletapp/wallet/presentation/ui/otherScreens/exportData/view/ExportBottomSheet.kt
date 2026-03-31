package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.toDateString
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTimePicker.AppDatePickerDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.reportButton.PremiumExportButton
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportResult
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    viewModel: ExportViewModel,
    onDismiss: () -> Unit,
    onExportClick: (ExportConfig) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedAccountId by remember { mutableStateOf<String?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var startDateMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        )
    }
    var endDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onTertiary.copy(
                    0.5f
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        tonalElevation = 0.dp // Toza ko'rinish uchun
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 20.dp
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            HeaderSection(onDismiss)

            Spacer(Modifier.height(24.dp))

            SectionLabel(stringResource(R.string.section_period))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateItem(
                        label = stringResource(R.string.date_input_label_start),
                        date = startDateMillis.toDateString(),
                        onClick = { showStartPicker = true },
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        null,
                        tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    DateItem(
                        label = stringResource(R.string.date_input_label_end),
                        date = endDateMillis.toDateString(),
                        onClick = { showEndPicker = true },
                        modifier = Modifier.weight(1f),
                        isEnd = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel(stringResource(R.string.section_transaction_type))
            SegmentedTypeSelector(
                selectedType = selectedType,
                onTypeSelected = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.setTransactionType(it)
                }
            )

            Spacer(Modifier.height(24.dp))

            HorizontalFilterSection(
                label = stringResource(R.string.section_category),
                items = categories.map { it.id to it.name },
                selectedId = selectedCategoryId,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedCategoryId = it
                }
            )

            Spacer(Modifier.height(24.dp))

            HorizontalFilterSection(
                label = stringResource(R.string.section_account),
                items = accounts.map { it.id to it.name },
                selectedId = selectedAccountId,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedAccountId = it
                }
            )

            Spacer(Modifier.height(40.dp))

            PremiumExportButton(
                isLoading = exportState is ExportResult.Loading,
                onExportTypeSelected = { selectedFormat -> // Mana bu yerda format keladi
                    onExportClick(
                        ExportConfig(
                            startDate = startDateMillis,
                            endDate = endDateMillis,
                            categoryId = selectedCategoryId,
                            accountId = selectedAccountId,
                            format = selectedFormat // <--- PDF o'rniga tanlangan formatni qo'yamiz
                        )
                    )
                }
            )
        }
    }

    // Dialogs
    if (showStartPicker) AppDatePickerDialog(
        startDateMillis,
        { startDateMillis = it },
        { showStartPicker = false })
    if (showEndPicker) AppDatePickerDialog(
        endDateMillis,
        { endDateMillis = it },
        { showEndPicker = false })
}


@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
    )
}


@Composable
fun HorizontalFilterSection(
    label: String,
    items: List<Pair<String, String>>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
) {
    Column {
        SectionLabel(label)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                SelectablePremiumChip(
                    selected = selectedId == null,
                    label = stringResource(Strings.type_all),
                    onClick = { onSelect(null) })
            }
            items(items) { (id, name) ->
                SelectablePremiumChip(
                    selected = selectedId == id,
                    label = name,
                    onClick = { onSelect(id) })
            }
        }
    }
}
