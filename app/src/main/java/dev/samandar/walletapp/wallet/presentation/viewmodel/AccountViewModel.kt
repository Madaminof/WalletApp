package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.usecase.account.AddAccount
import dev.samandar.walletapp.wallet.domain.usecase.account.DeleteAccount
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// --- UI States ---

data class AccountScreenState(
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val currentCurrency: String = "UZS",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class AddAccountFormState(
    val name: String = "",
    val type: AccountType = AccountType.CASH,
    val balance: String = "",
    val currencyCode: String = "UZS",
    val colorHex: String = "#4CAF50",
    val cardNumber: String = "",
    val cardProvider: String = "UZCARD",
    val isDefault: Boolean = false,
    val isDialogOpen: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccount,
    private val deleteAccountUseCase: DeleteAccount,
    private val getAllAccountsUseCase: GetAllAccounts,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    private val _formState = MutableStateFlow(AddAccountFormState())
    val formState: StateFlow<AddAccountFormState> = _formState.asStateFlow()

    val uiState: StateFlow<AccountScreenState> = combine(
        getAllAccountsUseCase(),
        ratesFlow,
        currentCurrencyFlow
    ) { accounts, rates, currentCurrency ->

        var total = 0.0
        val convertedAccounts = accounts.map { account ->
            // UI uchun balansni konvertatsiya qilishda haqiqiy valyuta balansidan (Konverter) foydalanamiz
            // Agar account USD bo'lsa, konverterda USD turibdi. Shuni tanlangan UI valyutasiga o'giramiz.
            val displayBalance = CurrencyEvaluator.convert(
                amount = account.amountCurrencyKonverter, // 👈 Accountning real balansi
                rates = rates,
                currentCurrency = currentCurrency
            )
            total += displayBalance

            account.copyAccountWithBalance(displayBalance)
        }

        AccountScreenState(
            accounts = convertedAccounts,
            totalBalance = total,
            currentCurrency = currentCurrency,
            isLoading = false
        )
    }
        .catch { e ->
            emit(AccountScreenState(error = "Xatolik: ${e.localizedMessage}", isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountScreenState(isLoading = true)
        )

    fun onFormEvent(event: AccountFormEvent) {
        when (event) {
            is AccountFormEvent.NameChanged -> _formState.update { it.copy(name = event.name) }
            is AccountFormEvent.TypeChanged -> _formState.update { it.copy(type = event.type) }
            is AccountFormEvent.BalanceChanged -> _formState.update { it.copy(balance = event.balance) }
            is AccountFormEvent.CurrencyChanged -> _formState.update { it.copy(currencyCode = event.code) }
            is AccountFormEvent.CardNumberChanged -> _formState.update { it.copy(cardNumber = event.number) }
            is AccountFormEvent.DefaultChanged -> _formState.update { it.copy(isDefault = event.isDefault) }
            is AccountFormEvent.ToggleDialog -> _formState.update { it.copy(isDialogOpen = event.isOpen) }
            AccountFormEvent.SaveAccount -> saveNewAccount()
        }
    }

    private fun saveNewAccount() {
        val form = _formState.value
        val amount = form.balance.toDoubleOrNull() ?: 0.0
        if (form.name.isBlank()) return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }

            val newAccount = when (form.type) {
                AccountType.CASH -> Account.Cash(
                    id = UUID.randomUUID().toString(),
                    name = form.name,
                    balance = amount,
                    amountCurrencyKonverter = amount, // 👈 YANGI MAYDON QO'SHILDI
                    currencyCode = form.currencyCode,
                    colorHex = form.colorHex,
                    iconResId = null,
                    isDefault = form.isDefault
                )
                AccountType.CARD -> Account.Card(
                    id = UUID.randomUUID().toString(),
                    name = form.name,
                    balance = amount,
                    amountCurrencyKonverter = amount, // 👈 YANGI MAYDON QO'SHILDI
                    currencyCode = form.currencyCode,
                    colorHex = form.colorHex,
                    iconResId = null,
                    cardNumber = form.cardNumber,
                    cardProvider = form.cardProvider,
                    isDefault = form.isDefault
                )
            }

            addAccountUseCase(newAccount)
            _formState.update { AddAccountFormState(isDialogOpen = false) }
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            addAccountUseCase(account)
        }
    }
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            addAccountUseCase(account)
        }
    }



    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            deleteAccountUseCase(account)
        }
    }

    private fun Account.copyAccountWithBalance(newBalance: Double): Account {
        return when (this) {
            is Account.Cash -> this.copy(balance = newBalance)
            is Account.Card -> this.copy(balance = newBalance)
        }
    }
}

sealed class AccountFormEvent {
    data class NameChanged(val name: String) : AccountFormEvent()
    data class TypeChanged(val type: AccountType) : AccountFormEvent()
    data class BalanceChanged(val balance: String) : AccountFormEvent()
    data class CurrencyChanged(val code: String) : AccountFormEvent()
    data class CardNumberChanged(val number: String) : AccountFormEvent()
    data class DefaultChanged(val isDefault: Boolean) : AccountFormEvent()
    data class ToggleDialog(val isOpen: Boolean) : AccountFormEvent()
    object SaveAccount : AccountFormEvent()
}