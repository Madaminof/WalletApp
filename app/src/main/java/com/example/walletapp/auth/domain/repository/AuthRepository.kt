package com.example.walletapp.auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<FirebaseUser?>
    fun getCurrentUser(): FirebaseUser?
    suspend fun firebaseSignInWithCredential(idToken: String): Result<FirebaseUser?>
    suspend fun signOut()
}
