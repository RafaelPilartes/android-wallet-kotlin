package com.example.wallet.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.entities.User
import com.example.wallet.data.repositories.UserRepository
import kotlinx.coroutines.launch

class CadastroViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Verifique se os campos estão preenchidos
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    onError("Por favor, preencha todos os campos.")
                    return@launch
                }

                // Insira o usuário no banco de dados
                val newUser = User(name = name, email = email, password = password)
                userRepository.insertUser(newUser)
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao cadastrar usuário: ${e.message}")
            }
        }
    }
}