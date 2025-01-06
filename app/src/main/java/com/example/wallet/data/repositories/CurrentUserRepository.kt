package com.example.wallet.data.repositories

import com.example.wallet.data.dao.CurrentUserDao
import com.example.wallet.data.entities.CurrentUser

class CurrentUserRepository(private val currentUserDao: CurrentUserDao) {
    suspend fun setCurrentUser(currentUser: CurrentUser) = currentUserDao.setCurrentUser(currentUser)
    suspend fun getCurrentUser() = currentUserDao.getCurrentUser()
    suspend fun clearCurrentUser() = currentUserDao.clearCurrentUser()
}