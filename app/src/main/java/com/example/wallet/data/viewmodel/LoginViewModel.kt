package com.example.wallet.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.repositories.CurrentUserRepository
import com.example.wallet.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (email.isBlank() || password.isBlank()) {
                    onError("Por favor, preencha todos os campos.")
                    return@launch
                }

                val user = userRepository.loginUser(email, password)
                if (user != null) {
                    onSuccess()
                } else {
                    onError("Email ou senha incorretos.")
                }
            } catch (e: Exception) {
                onError("Erro ao fazer login: ${e.message}")
            }
        }
    }
}