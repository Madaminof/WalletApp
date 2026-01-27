package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.usecase.account.AddAccount
import dev.samandar.walletapp.wallet.domain.usecase.account.DeleteAccount
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountCardState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccount,
    private val deleteAccountUseCase: DeleteAccount,
    private val getAllAccountsUseCase: GetAllAccounts
) : ViewModel() {

    private val _cardState = MutableStateFlow(AccountCardState(isLoading = true))
    val cardState: StateFlow<AccountCardState> = _cardState.asStateFlow()

    init {
        loadAccounts()
    }


    private fun loadAccounts() {
        viewModelScope.launch {
            _cardState.value = _cardState.value.copy(isLoading = true, error = null)

            getAllAccountsUseCase()
                .catch { exception ->
                    // Xatolik yuz berganda
                    _cardState.value = _cardState.value.copy(
                        isLoading = false,
                        error = "Hisoblarni yuklashda xato: ${exception.localizedMessage}"
                    )
                }
                .collect { accountList ->
                    // Muovaffaqiyatli yuklanganda
                    _cardState.value = _cardState.value.copy(
                        accounts = accountList,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            addAccountUseCase(account)
        }
    }

    fun deleteAccount(account: Account){
        viewModelScope.launch {
            deleteAccountUseCase(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            addAccountUseCase(account) // UseCase bazada ID borligini ko'rib, uni yangilaydi
        }
    }

    // Hisob qo'shish dialogini boshqarish uchun (Ixtiyoriy, lekin UI uchun kerak)
    private val _isAddAccountDialogOpen = MutableStateFlow(false)
    val isAddAccountDialogOpen: StateFlow<Boolean> = _isAddAccountDialogOpen.asStateFlow()

    fun onAddAccountClick() {
        _isAddAccountDialogOpen.value = true
    }

    fun onAddAccountDialogDismiss() {
        _isAddAccountDialogOpen.value = false
    }
}