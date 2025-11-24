package com.example.walletapp.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.auth.domain.repository.AuthRepository
import com.example.walletapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.walletapp.auth.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val uid: String, val email: String?) : AuthState
    data class Error(val message: String) : AuthState
}
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val currentUser = repository.currentUserFlow

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val res = repository.firebaseSignInWithCredential(idToken)

            if (res.isSuccess) {
                val user = res.getOrNull()
                _state.value = if (user != null) {
                    AuthState.Success(uid = user.uid, email = user.email)
                } else {
                    AuthState.Error("User is null after sign-in. Please try again.")
                }
            } else {
                _state.value = AuthState.Error(res.exceptionOrNull()?.localizedMessage ?: "Unknown error")
            }
        }
    }
    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _state.value = AuthState.Idle
        }
    }
    fun setStateError(message: String) {
        _state.value = AuthState.Error(message)
    }
    fun setStateIdle() {
        _state.value = AuthState.Idle
    }
}