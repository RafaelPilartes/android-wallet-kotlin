package com.example.wallet.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun parseDateToTimestamp(date: String): Long? {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.parse(date)?.time
    } catch (e: Exception) {
        null
    }
}
