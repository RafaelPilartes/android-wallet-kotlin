package com.example.wallet.data.dao

import androidx.room.*
import com.example.wallet.data.entities.CurrentUser

@Dao
interface CurrentUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentUser(currentUser: com.example.wallet.data.entities.CurrentUser)

    @Query("SELECT * FROM current_user LIMIT 1")
    suspend fun getCurrentUser(): CurrentUser?

    @Query("DELETE FROM current_user")
    suspend fun clearCurrentUser()
}