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
import com.example.wallet.data.entities.Transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.GreenLight

@Composable
fun DespesasScreen(
    viewModel: TransactionViewModel,
    userId: Int,
    onCreateExpense: () -> Unit,
    onEditExpense: (Transaction) -> Unit
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQueryExpenses.collectAsState(initial = "")

    LaunchedEffect(Unit) {
        viewModel.fetchExpenses(userId)
    }

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(36.dp))

        // Campo de pesquisa
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> viewModel.searchExpenses(userId, query) },
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
                        IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                            Icon(
                                imageVector = Icons.Default.Delete, // Substitua pelo ícone de deletar desejado
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