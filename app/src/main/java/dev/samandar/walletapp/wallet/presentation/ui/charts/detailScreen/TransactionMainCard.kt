package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit.AmountEditContent
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit.EditType
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit.EditWrapperDialog
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit.NoteEditContent
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.edit.SelectionList
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime.PremiumDateTimePickerDialog
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun TransactionMainCard(
    transaction: Transaction,
    categories: List<Category>,
    accounts: List<Account>,
    onEdit: (Transaction) -> Unit
    ) {

    val categoryName = getTranslatedName(transaction.category.name)
    val accountName = getTranslatedName(transaction.account.name)

    val formattedDate = remember(transaction.date) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = transaction.date
        val transactionYear = calendar.get(Calendar.YEAR)

        val pattern = if (currentYear == transactionYear) {
            "d-MMM, HH:mm"
        } else {
            "d-MMM yyyy, HH:mm"
        }

        SimpleDateFormat(pattern, Locale.getDefault()).format(transaction.date)
    }
    val isExpense = transaction.type == TransactionType.EXPENSE

    var activeEditType by remember { mutableStateOf(EditType.NONE) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(
                        iconRes = transaction.category.iconResId,
                        color = Color(transaction.category.colorArgb),

                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = categoryName.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.9f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp
                    )
                }

                Text(
                    text = "${if (isExpense) "-" else "+"}${FormatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isExpense) expenseColor else incomeColor
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            activeEditType = EditType.AMOUNT
                        }
                        .padding(vertical = 4.dp),
                    maxLines = 1 ,
                    fontSize = 14.sp
                )
            }

            DashedDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.6f)
            )

            NoteDetailItem(
                label = stringResource(R.string.note_label),
                note = transaction.note,
                onClick = { activeEditType = EditType.NOTE }
            )

            DetailRowItem(
                label = stringResource(R.string.category_label_title),
                value = categoryName.toString(),
                showArrow = true,
                onClick = { activeEditType = EditType.CATEGORY }
            )

            DetailRowItem(
                label = stringResource(R.string.detail_account),
                value =accountName.toString(),
                showArrow = true,
                onClick = { activeEditType = EditType.ACCOUNT }
            )

            DetailRowItem(
                label = stringResource(R.string.detail_date_time),
                value = formattedDate.format(Date(transaction.date)),
                showArrow = true,
                onClick = { activeEditType = EditType.DATE }
            )
        }

        if (activeEditType != EditType.NONE) {
            if (activeEditType == EditType.DATE) {
                PremiumDateTimePickerDialog(
                    initialDateTime = transaction.date,
                    onConfirm = { newTimestamp ->
                        onEdit(transaction.copy(date = newTimestamp))
                        activeEditType = EditType.NONE
                    },
                    onDismiss = { activeEditType = EditType.NONE }
                )
            }else{
                EditWrapperDialog(
                    title = when (activeEditType) {
                        EditType.CATEGORY -> stringResource(R.string.title_edit_category)
                        EditType.ACCOUNT -> stringResource(R.string.title_edit_account)
                        EditType.AMOUNT -> stringResource(R.string.title_edit_amount)
                        EditType.NOTE -> stringResource(R.string.title_edit_note)
                        else -> ""
                    },
                    onDismiss = { activeEditType = EditType.NONE }
                ) {
                    when (activeEditType) {
                        EditType.CATEGORY -> SelectionList(
                            items = categories,
                            getName = { it.name },
                            getIcon = { it.iconResId },
                            getColor = {Color(it.colorArgb)},
                            initialSelected = transaction.category,
                            onDismiss = { activeEditType = EditType.NONE },
                            onSave = { newCategory ->
                                onEdit(transaction.copy(category = newCategory))
                                activeEditType = EditType.NONE
                            }

                        )

                        EditType.ACCOUNT -> SelectionList(
                            items = accounts,
                            getName = { it.name },
                            getIcon = {it.iconResId},
                            getColor = { account ->
                                account.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Gray
                            },
                            initialSelected = transaction.account,
                            onDismiss = { activeEditType = EditType.NONE },
                            onSave = { newAccount ->
                                onEdit(transaction.copy(account = newAccount))
                                activeEditType = EditType.NONE
                            }
                        )

                        EditType.AMOUNT -> AmountEditContent(
                            initialAmount = transaction.amount,
                            onSave = {
                                onEdit(transaction.copy(amount = it)); activeEditType =
                                EditType.NONE
                            },
                            onDismiss = {activeEditType = EditType.NONE}
                        )

                        EditType.NOTE -> NoteEditContent(
                            initialNote = transaction.note,
                            onSave = {
                                onEdit(transaction.copy(note = it)); activeEditType = EditType.NONE
                            },
                            onDismiss = {activeEditType = EditType.NONE}

                        )

                        else -> {}
                    }
                }
            }

        }
    }
}