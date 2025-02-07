package com.example.wallet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import com.example.wallet.data.entities.Transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.room.PrimaryKey
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.GreenLight
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DespesasScreen(
    viewModel: TransactionViewModel,
    userId: Any,
    onCreateExpense: () -> Unit,
    onEditExpense: (Transaction) -> Unit
) {
    // Room
    // val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQueryExpenses.collectAsState(initial = "")

    // Firebase
    var expenses by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Room
        // viewModel.fetchExpenses(userId)

        // Firebase
        try {
            fetchExpenses(userId) { fetchedExpenses ->
                println("Expenses fetched successfully")
                expenses = fetchedExpenses
            }
        } catch (e: Exception) {
            println("Error in LaunchedEffect: $e")
        }
    }

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(36.dp))

        // Campo de pesquisa
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> null },
            label = { Text("Pesquisar despesas") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de despesas
        if (expenses.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f)) {
                items(expenses) { expense ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Detalhes da despesa
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onEditExpense(expense) }
                        ) {
                            Text(expense.title ?: "Sem título", style = MaterialTheme.typography.bodyMedium)
                            Text(expense.description ?: "Sem descrição", style = MaterialTheme.typography.bodySmall)
                            Text("${expense.value ?: 0.0} AKZ", style = MaterialTheme.typography.bodySmall)
                        }

                        // Botão Deletar
                        // Room
                        // IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                        // Firebase
                        IconButton(onClick = {
                            deleteExpense(
                                expenseId = expense.id ?: "0", // Passe o ID da despesa
                                onSuccess = {
                                    println("Despesa deletada com sucesso!")
                                    // Atualize a lista após a exclusão
                                    fetchExpenses(userId) { updatedExpenses ->
                                        expenses = updatedExpenses
                                    }
                                },
                                onFailure = { exception ->
                                    println("Erro ao deletar despesa: $exception")
                                }
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar despesa",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text("Nenhuma despesa encontrada.")
            }
        }

        // Botão para adicionar despesa
        Button(
            onClick = onCreateExpense,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
        ) {
            Text("Adicionar Nova Despesa")
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

// Firebase
fun fetchExpenses(userId: Any, onExpensesFetched: (List<Transaction>) -> Unit) {
    FirebaseFirestore.getInstance().collection("transactions")
        .whereEqualTo("userId", userId)
        .whereEqualTo("type", "despesa")
        .get()
        .addOnSuccessListener { result ->
            try {
                val expenses = result.map { document ->
                    document.toObject(Transaction::class.java).apply {
                        println("Fetched Transaction: $this") // Log para verificar os dados
                        id = document.id // Atribui o ID do documento
                    }
                }
                onExpensesFetched(expenses)
            } catch (e: Exception) {
                println("Error mapping expenses: $e")
            }
        }
        .addOnFailureListener { e ->
            println("Error fetching expenses: $e")
        }
}

fun deleteExpense(expenseId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (expenseId.isBlank()) {
        println("Expense ID is blank or null!")
        onFailure(Exception("Invalid Expense ID"))
        return
    }

    FirebaseFirestore.getInstance().collection("transactions")
        .document(expenseId)
        .delete()
        .addOnSuccessListener {
            println("Expense deleted successfully: $expenseId")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            println("Error deleting expense: $exception")
            onFailure(exception)
        }
}
