package com.example.wallet.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallet.data.entities.Transaction
import com.example.wallet.data.repositories.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Transaction>>(emptyList())
    val expenses: StateFlow<List<Transaction>> get() = _expenses

    private val _incomes = MutableStateFlow<List<Transaction>>(emptyList())
    val incomes: StateFlow<List<Transaction>> get() = _incomes

    private val _searchQueryExpenses = MutableStateFlow("")
    val searchQueryExpenses: StateFlow<String> get() = _searchQueryExpenses

    private val _searchQueryIncomes = MutableStateFlow("")
    val searchQueryIncomes: StateFlow<String> get() = _searchQueryIncomes

    // Fetch despesas
    fun fetchExpenses(userId: Int) {
        viewModelScope.launch {
            repository.getExpenses(userId)
                .catch { emit(emptyList<Transaction>()) }
                .collect { fetchedExpenses ->
                    _expenses.value = fetchedExpenses
                }
        }
    }

    // Fetch ganhos
    fun fetchIncomes(userId: Int) {
        viewModelScope.launch {
            repository.getIncomes(userId)
                .catch { emit(emptyList<Transaction>()) }
                .collect { fetchedIncomes ->
                    _incomes.value = fetchedIncomes
                }
        }
    }

    // Search despesas
    fun searchExpenses(userId: Int, query: String) {
        _searchQueryExpenses.value = query
        viewModelScope.launch {
            repository.searchExpenses(userId, query)
                .catch { emit(emptyList<Transaction>()) }
                .collect { filteredExpenses ->
                    _expenses.value = filteredExpenses
                }
        }
    }

    // Search ganhos
    fun searchIncomes(userId: Int, query: String) {
        _searchQueryIncomes.value = query
        viewModelScope.launch {
            repository.searchIncomes(userId, query)
                .catch { emit(emptyList<Transaction>()) }
                .collect { filteredIncomes ->
                    _incomes.value = filteredIncomes
                }
        }
    }

    fun getTransactionById(expenseId: Int, onResult: (Transaction?) -> Unit) {
        viewModelScope.launch {
            val transaction = repository.getTransactionById(expenseId)
            onResult(transaction)
        }
    }


    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
            if (transaction.type == "despesa") {
                fetchExpenses(transaction.userId)
            } else {
                fetchIncomes(transaction.userId)
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            if (transaction.type == "despesa") {
                fetchExpenses(transaction.userId)
            } else {
                fetchIncomes(transaction.userId)
            }
        }
    }

    fun deleteExpense(expense: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(expense.id)
            fetchExpenses(expense.userId)
        }
    }
    fun deleteIncome(income: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(income.id)
            fetchIncomes(income.userId)
        }
    }


    private val _totalGains = MutableStateFlow(0.0)
    val totalGains: StateFlow<Double> get() = _totalGains

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> get() = _totalExpenses

    val currentBalance: StateFlow<Double> = combine(totalGains, totalExpenses) { gains, expenses ->
        gains - expenses
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun fetchTotalExpenses(userId: Int) {
        viewModelScope.launch {
            val total = repository.getTotalExpenses(userId)
            _totalExpenses.value = total
        }
    }

    fun fetchTotalGains(userId: Int) {
        viewModelScope.launch {
            val total = repository.getTotalGains(userId)
            _totalGains.value = total
        }
    }
}
