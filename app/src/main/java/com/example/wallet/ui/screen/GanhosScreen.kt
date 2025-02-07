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
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.GreenLight
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GanhosScreen(
    viewModel: TransactionViewModel,
    userId: Any,
    onCreateIncome: () -> Unit,
    // Room
    // onEditIncome: (Transaction) -> Unit
    // Firebase
    onEditIncome: (String) -> Unit
) {
    // Room
    // val incomes by viewModel.incomes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQueryIncomes.collectAsState(initial = "")

    // Firebase
    var incomes by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Room
        // viewModel.fetchIncomes(userId)

        // Firebase
        try {
            fetchIncomes(userId) { fetchedIncomes ->
                println("Incomes fetched successfully")
                incomes = fetchedIncomes
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
            // onValueChange = { query -> viewModel.searchIncomes(userId, query) },
            onValueChange = { query -> null },
            label = { Text("Pesquisar ganhos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de ganhos
        if (incomes.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f)) {
                items(incomes) { income ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Detalhes do ganho
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onEditIncome(income.id ?: "") }
                        ) {
                            Text(income.title ?: "Sem título", style = MaterialTheme.typography.bodyMedium)
                            Text(income.description ?: "Sem descrição", style = MaterialTheme.typography.bodySmall)
                            Text("${income.value ?: 0.0} AKZ", style = MaterialTheme.typography.bodySmall)
                        }

                        // Botão Deletar
                        // Room
                        // IconButton(onClick = { viewModel.deleteIncome(income) }) {
                        // Firebase
                        IconButton(onClick = {
                            deleteIncome(
                                incomeId = income.id ?: "0", // Passe o ID da ganho
                                onSuccess = {
                                    println("Ganho deletada com sucesso!")
                                    // Atualize a lista após a exclusão
                                    fetchIncomes(userId) { updatedIncome ->
                                        incomes = updatedIncome
                                    }
                                },
                                onFailure = { exception ->
                                    println("Erro ao deletar ganho: $exception")
                                }
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar ganho",
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
                Text("Nenhum ganho encontrado.")
            }
        }

        // Botão para adicionar ganho
        Button(
            onClick = onCreateIncome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
        ) {
            Text("Adicionar Novo Ganho")
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

// Firebase
fun fetchIncomes(userId: Any, onIncomesFetched: (List<Transaction>) -> Unit) {
    FirebaseFirestore.getInstance().collection("transactions")
        .whereEqualTo("userId", userId)
        .whereEqualTo("type", "ganho")
        .get()
        .addOnSuccessListener { result ->
            try {
                val incomes = result.map { document ->
                    document.toObject(Transaction::class.java).apply {
                        println("Fetched Transaction: $this") // Log para verificar os dados
                        id = document.id // Atribui o ID do documento
                    }
                }
                onIncomesFetched(incomes)
            } catch (e: Exception) {
                println("Error mapping incomes: $e")
            }
        }
        .addOnFailureListener { e ->
            println("Error fetching incomes: $e")
        }
}

fun deleteIncome(incomeId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (incomeId.isBlank()) {
        println("Income ID is blank or null!")
        onFailure(Exception("Invalid Income ID"))
        return
    }

    FirebaseFirestore.getInstance().collection("transactions")
        .document(incomeId)
        .delete()
        .addOnSuccessListener {
            println("Income deleted successfully: $incomeId")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            println("Error deleting income: $exception")
            onFailure(exception)
        }
}
