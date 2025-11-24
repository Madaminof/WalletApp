package com.example.walletapp.auth.data.repository

import android.util.Log
import com.example.walletapp.auth.data.local.User
import com.example.walletapp.auth.data.local.UserDao
import com.example.walletapp.auth.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    override val currentUserFlow = _currentUser.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override suspend fun firebaseSignInWithCredential(idToken: String): Result<FirebaseUser?> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                withContext(Dispatchers.IO) {
                    val userEntity = User(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email.orEmpty(),
                        displayName = firebaseUser.displayName,
                        photoUrl = firebaseUser.photoUrl.toString()
                    )
                    userDao.insert(userEntity)
                    Log.d("AuthRepo", "Foydalanuvchi Room keshiga saqlandi: ${firebaseUser.uid}")
                }
            }
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Firebase Sign-In xatosi: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signOut() = withContext(Dispatchers.IO) {
        val userIdToClear = firebaseAuth.uid
        googleSignInClient.signOut().await()

        firebaseAuth.signOut()

        if (userIdToClear != null) {

            Log.d("AuthRepo", "Foydalanuvchi keshdan tozalash kerak (deleteUser chaqirilishi kerak)")
        }
    }
}