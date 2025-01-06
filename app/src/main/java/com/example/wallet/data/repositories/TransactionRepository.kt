package com.example.wallet.data.repositories

import com.example.wallet.data.dao.TransactionDAO
import com.example.wallet.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDAO: TransactionDAO) {

    // Despesas
    fun getExpenses(userId: Int): Flow<List<Transaction>> {
        return transactionDAO.getExpenses(userId)
    }

    fun searchExpenses(userId: Int, query: String): Flow<List<Transaction>> {
        return transactionDAO.searchExpenses(userId, query)
    }

    // Ganhos
    fun getIncomes(userId: Int): Flow<List<Transaction>> {
        return transactionDAO.getIncomes(userId)
    }

    fun searchIncomes(userId: Int, query: String): Flow<List<Transaction>> {
        return transactionDAO.searchIncomes(userId, query)
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDAO.getTransactionById(id)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDAO.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDAO.updateTransaction(
            id = transaction.id,
            title = transaction.title,
            description = transaction.description,
            value = transaction.value,
            date = transaction.date,
            type = transaction.type
        )
    }

    suspend fun deleteTransaction(id: Int) {
        transactionDAO.deleteTransaction(id)
    }

    suspend fun getTotalExpenses(userId: Int): Double {
        return transactionDAO.getTotalExpenses(userId) ?: 0.0
    }

    suspend fun getTotalGains(userId: Int): Double {
        return transactionDAO.getTotalGains(userId) ?: 0.0
    }

}
