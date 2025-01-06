package com.example.wallet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet.data.entities.Transaction
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.GreenLight
import com.example.wallet.ui.theme.RedBase

@Composable
fun DespesasEditarScreen(
    viewModel: TransactionViewModel,
    expense: Transaction?,
    onNavigateBack: () -> Unit
) {
    if (expense == null) {
        Text("Erro: Despesa não encontrada.")
        return
    }

    var title by remember { mutableStateOf(expense.title ?: "") }
    var description by remember { mutableStateOf(expense.description ?: "") }
    var value by remember { mutableStateOf(expense.value.toString()) }
    var date by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Editar Despesa", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Valor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Data (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateTransaction(
                    expense.copy(
                        title = title,
                        description = description,
                        value = value.toDoubleOrNull() ?: 0.0,
                        date = System.currentTimeMillis()
                    )
                )
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
        ) {
            Text("Salvar Alterações")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = RedBase)
        ) {
            Text("Cancelar")
        }
    }
}
