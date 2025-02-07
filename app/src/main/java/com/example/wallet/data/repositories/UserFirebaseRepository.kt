package com.example.wallet.data.repositories

import com.google.firebase.auth.FirebaseAuth

class UserFirebaseRepository {
    fun getCurrentUserDetails(): Pair<String?, String?> {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        return if (firebaseUser != null) {
            val userId = firebaseUser.uid
            val userName = firebaseUser.displayName ?: "Usuário Desconhecido"
            Pair(userId, userName)
        } else {
            Pair(null, null)
        }
    }
}
