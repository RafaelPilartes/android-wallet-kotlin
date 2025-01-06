package com.example.wallet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.wallet.R
import com.example.wallet.ui.componet.WalletButton
import com.example.wallet.ui.theme.GreenBase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.wallet.data.database.AppDatabase
import com.example.wallet.data.repositories.UserRepository
import com.example.wallet.data.viewmodel.CadastroViewModel
import com.example.wallet.data.viewmodel.LoginViewModel
import com.example.wallet.ui.componet.GradientBox
import com.example.wallet.utils.isSmallScreenHeight
import com.example.wallet.utils.rememberImeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    userRepository: UserRepository,
    onNavigateToCadastro: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email = remember { mutableStateOf(TextFieldValue()) }
    var password = remember { mutableStateOf(TextFieldValue()) }
    val isImeVisible by rememberImeState()

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


    }

    GradientBox(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val animatedUpperSectionRatio by animateFloatAsState(
                targetValue = if (isImeVisible) 0f else 0.35f,
                label = "",
            )
            AnimatedVisibility(visible = !isImeVisible, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedUpperSectionRatio),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bem-vindo de volta",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSmallScreenHeight()) {
                    Spacer(modifier = Modifier.fillMaxSize(0.05f))
                } else {
                    Spacer(modifier = Modifier.fillMaxSize(0.1f))
                }
                Text("Tela de Login", style = MaterialTheme.typography.headlineLarge)

                if (isSmallScreenHeight()) {
                    Spacer(modifier = Modifier.fillMaxSize(0.05f))
                } else {
                    Spacer(modifier = Modifier.fillMaxSize(0.1f))
                }
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenBase,
                        focusedLabelColor = GreenBase
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenBase,
                        focusedLabelColor = GreenBase
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                WalletButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Entrar",
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val isSuccess = userRepository.loginUser(email.value.text, password.value.text)
                            if (isSuccess) {
                                withContext(Dispatchers.Main) {
                                    onNavigateToHome()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Credenciais inválidas"
                                }
                            }
                        }
                    }
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onNavigateToCadastro) {
                    Text(text = "Não tem conta? Cadastre-se",
                        color = GreenBase
                    )
                }
            }
        }
    }
}
