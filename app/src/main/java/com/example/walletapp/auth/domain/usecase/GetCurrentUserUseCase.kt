package com.example.walletapp.auth.domain.usecase

import com.example.walletapp.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase(private val repo: AuthRepository) {
    operator fun invoke(): FirebaseUser? = repo.getCurrentUser()
}
