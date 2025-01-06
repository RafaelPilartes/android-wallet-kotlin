package com.example.wallet.data.repositories

import com.example.wallet.data.dao.CurrentUserDao
import com.example.wallet.data.dao.UserDao
import com.example.wallet.data.entities.CurrentUser
import com.example.wallet.data.entities.User

class UserRepository(private val userDao: UserDao, private val currentUserDao: CurrentUserDao) {
    suspend fun loginUser(email: String, password: String): Boolean {
        val user = userDao.loginUser(email, password)
        return if (user != null) {
            val currentUser = CurrentUser(
                id = user.id, // ID fixo para usuário corrente
                name = user.name,
                email = user.email,
                password = user.password
            )
            currentUserDao.setCurrentUser(currentUser)
            true
        } else {
            false // Login inválido
        }
    }
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun getAllUsers() = userDao.getAllUsers()
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun getCurrentUser(): CurrentUser? = currentUserDao.getCurrentUser()

    suspend fun logoutCurrentUser() = currentUserDao.clearCurrentUser()
}