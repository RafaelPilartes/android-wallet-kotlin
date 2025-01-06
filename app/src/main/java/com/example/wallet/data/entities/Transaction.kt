package com.example.wallet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val description: String,
    val value: Double,
    val date: Long,
    val type: String // "despesa" ou "ganho"
)
