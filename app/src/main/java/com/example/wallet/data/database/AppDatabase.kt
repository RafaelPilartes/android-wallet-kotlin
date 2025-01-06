package com.example.wallet.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.wallet.data.dao.UserDao
import com.example.wallet.data.dao.CurrentUserDao
import com.example.wallet.data.dao.TransactionDAO
import com.example.wallet.data.entities.User
import com.example.wallet.data.entities.Transaction
import com.example.wallet.data.entities.CurrentUser

@Database(
    entities = [User::class, Transaction::class, CurrentUser::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDAO
    abstract fun currentUserDao(): CurrentUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}