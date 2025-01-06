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
fun GanhosScreen(
    viewModel: TransactionViewModel,
    userId: Int,
    onCreateIncome: () -> Unit,
    onEditIncome: (Transaction) -> Unit
) {
    val incomes by viewModel.incomes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQueryIncomes.collectAsState(initial = "")

    LaunchedEffect(Unit) {
        viewModel.fetchIncomes(userId)
    }

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(36.dp))

        // Campo de pesquisa
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> viewModel.searchIncomes(userId, query) },
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
                                .clickable { onEditIncome(income) }
                        ) {
                            Text(income.title ?: "Sem título", style = MaterialTheme.typography.bodyMedium)
                            Text(income.description ?: "Sem descrição", style = MaterialTheme.typography.bodySmall)
                            Text("${income.value ?: 0.0} AKZ", style = MaterialTheme.typography.bodySmall)
                        }

                        // Botão Deletar
                        IconButton(onClick = { viewModel.deleteIncome(income) }) {
                            Icon(
                                imageVector = Icons.Default.Delete, // Substitua pelo ícone de deletar desejado
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
