package com.example.wallet.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.wallet.data.entities.User
import com.example.wallet.data.repositories.UserRepository
import com.example.wallet.ui.componet.GradientBox
import com.example.wallet.ui.componet.WalletButton
import com.example.wallet.ui.theme.GreenBase
import com.example.wallet.utils.isSmallScreenHeight
import com.example.wallet.utils.rememberImeState
import kotlinx.coroutines.launch

@Composable
fun CadastroScreen(
    userRepository: UserRepository,    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val isImeVisible by rememberImeState()

    val showError = remember { mutableStateOf<String?>(null) }
    val showSuccess = remember { mutableStateOf(false) }

    // Resetar sucesso após 3 segundos
    if (showSuccess.value) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            showSuccess.value = false
        }
    }

    // Função para validar email
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Função para gerenciar o cadastro
    fun handleUserRegistration() {
        val userName = name.text.trim()
        val userEmail = email.text.trim()
        val userPassword = password.text.trim()

        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
            showError.value = "Todos os campos são obrigatórios!"
            return
        }

        if (!isValidEmail(userEmail)) {
            showError.value = "Email inválido!"
            return
        }

        scope.launch {
            try {
                val userAlreadyExists = userRepository.getAllUsers()
                    .any { it.email == userEmail }

                if (userAlreadyExists) {
                    showError.value = "Já existe um usuário com este email!"
                } else {
                    userRepository.insertUser(
                        User(
                            name = userName,
                            email = userEmail,
                            password = userPassword
                        )
                    )
                    showSuccess.value = true
                    showError.value = null
                    name = TextFieldValue()
                    email = TextFieldValue()
                    password = TextFieldValue()
                }
            } catch (e: Exception) {
                showError.value = "Erro ao cadastrar usuário: ${e.message}"
            }
        }
    }

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
                        text = "Bem-vindo ao Wallet",
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
                Text("Tela de Registro", style = MaterialTheme.typography.headlineLarge)

                if (isSmallScreenHeight()) {
                    Spacer(modifier = Modifier.fillMaxSize(0.05f))
                } else {
                    Spacer(modifier = Modifier.fillMaxSize(0.1f))
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenBase,
                        focusedLabelColor = GreenBase
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    value = password,
                    onValueChange = { password = it },
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
                    text = "Cadastrar",
                    onClick = {
                        handleUserRegistration()
                    }

                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showError.value != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = showError.value!!, color = Color.Red)
                }

                if (showSuccess.value) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Usuário cadastrado com sucesso!", color = GreenBase)
                }


                TextButton(onClick = onNavigateBack) {
                    Text(text = "Já tem conta? Login",
                        color = GreenBase
                    )
                }
            }
        }
    }
}
