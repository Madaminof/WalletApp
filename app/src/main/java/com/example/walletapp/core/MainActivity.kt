package com.example.walletapp.core

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.walletapp.ui.theme.WalletAppTheme
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import com.example.walletapp.auth.presentation.AuthViewModel
import com.example.walletapp.navigation.NavGraph
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var googleSignInClient: GoogleSignInClient

    private val authViewModel: AuthViewModel by viewModels()

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(Exception::class.java)
                    val idToken = account?.idToken
                    if (idToken != null) {
                        authViewModel.signInWithGoogleIdToken(idToken)
                    }
                } catch (_: Exception) {}
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WalletAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
                    val addAccountViewModel: AccountViewModel = hiltViewModel()
                    val budgetViewModel: BudgetViewModel = hiltViewModel()

                    NavGraph(
                        onSignInClicked = {
                            val intent = googleSignInClient.signInIntent
                            signInLauncher.launch(intent)
                        },
                        viewModel = homeViewModel,
                        viewModel1 = addTransactionViewModel,
                        authViewModel = authViewModel,
                        addAccountViewModel = addAccountViewModel,
                        budgetViewModel = budgetViewModel,

                    )
                }
            }
        }
    }
}
