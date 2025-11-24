package com.example.walletapp.wallet.presentation.ui.otherScreens.goals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar


@Composable
fun GoalsScreen(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Goals",
                onBackClick = {navController.popBackStack()}
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Goals Screen",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
