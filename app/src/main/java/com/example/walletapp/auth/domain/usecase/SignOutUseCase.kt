package com.example.walletapp.auth.domain.usecase

import com.example.walletapp.auth.domain.repository.AuthRepository


class SignOutUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.signOut()
}
