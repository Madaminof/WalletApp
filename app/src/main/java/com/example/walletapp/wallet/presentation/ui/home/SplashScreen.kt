package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walletapp.R
import com.example.walletapp.auth.presentation.AuthViewModel
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    AppStatusBarColor(MaterialTheme.colorScheme.background)

    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    val scale = remember { Animatable(0.5f) }
    var isContentVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseOutCubic
            )
        )
    }
    LaunchedEffect(currentUser) {
        delay(1500)
        isContentVisible = false
        if (currentUser != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    AnimatedVisibility(
        visible = isContentVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { fullHeight -> fullHeight / 2 },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale.value)
            ) {
                Icon(
                    painter = painterResource(R.drawable.wallet_icon2),
                    contentDescription = "Wallet App Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                LinearProgressIndicator(
                    modifier = Modifier.width(180.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    trackColor = MaterialTheme.colorScheme.primary
                )

            }
        }
    }
}