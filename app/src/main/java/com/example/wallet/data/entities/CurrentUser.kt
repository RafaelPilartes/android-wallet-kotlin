package com.example.wallet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class CurrentUser(
    @PrimaryKey val id: Int = 1, // Sempre apenas 1 registro para o usuário corrente
    val name: String,
    val email: String,
    val password: String
)