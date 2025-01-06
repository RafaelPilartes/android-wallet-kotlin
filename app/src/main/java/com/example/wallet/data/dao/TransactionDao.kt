package com.example.wallet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wallet.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    // Despesas
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'despesa'")
    fun getExpenses(userId: Int): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND type = 'despesa' 
        AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
    """)
    fun searchExpenses(userId: Int, query: String): Flow<List<Transaction>>

    // Ganhos
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'ganho'")
    fun getIncomes(userId: Int): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND type = 'ganho' 
        AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
    """)
    fun searchIncomes(userId: Int, query: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Int)

    @Query("""
        UPDATE transactions
        SET title = :title, description = :description, value = :value, date = :date, type = :type
        WHERE id = :id
    """)
    suspend fun updateTransaction(id: Int, title: String, description: String, value: Double, date: Long, type: String)

    @Query("SELECT SUM(value) FROM transactions WHERE userId = :userId AND type = 'despesa'")
    suspend fun getTotalExpenses(userId: Int): Double?

    @Query("SELECT SUM(value) FROM transactions WHERE userId = :userId AND type = 'ganho'")
    suspend fun getTotalGains(userId: Int): Double?

}
