package com.example.wallet.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.wallet.R
import com.example.wallet.ui.theme.GreenLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier, isLoggedIn: Boolean, onNavigateToLogin: () -> Unit, onNavigateToHome: () -> Unit) {
    LaunchedEffect(key1 = Unit) {
        delay(3_000)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = modifier
            .background(GreenLight)
            .fillMaxSize(),
    ) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_wallet_logo),
            contentDescription = "Imagem Logo"
        )
        Image(
            modifier = Modifier.align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.bg_splash_screen),
            contentDescription = "Imagem Background"
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(isLoggedIn = true, onNavigateToLogin = {}, onNavigateToHome = {})
}