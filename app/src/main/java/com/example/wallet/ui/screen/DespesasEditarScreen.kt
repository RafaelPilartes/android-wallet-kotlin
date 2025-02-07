package com.example.wallet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import com.example.wallet.data.entities.Transaction
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.GreenLight
import com.example.wallet.ui.theme.RedBase
import com.example.wallet.utils.parseDateToTimestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DespesasEditarScreen(
    viewModel: TransactionViewModel,
    // expense: Transaction?,
    expenseId: String,
    onNavigateBack: () -> Unit
) {
    // if (expense == null) {
        // Text("Erro: Despesa não encontrada.")
        // return
    // }

    // var title by remember { mutableStateOf(expense.title ?: "") }
    // var description by remember { mutableStateOf(expense.description ?: "") }
    // var value by remember { mutableStateOf(expense.value.toString()) }
    // var date by remember { mutableStateOf("") }

    // Firebase
    var expense by remember { mutableStateOf<com.example.wallet.ui.screen.Transaction?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isLoadingData by remember { mutableStateOf(true) }
    var errorMessageData by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(expenseId) {
        FirebaseFirestore.getInstance()
            .collection("transactions")
            .document(expenseId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fetchedExpense = document.toObject(com.example.wallet.ui.screen.Transaction::class.java)
                    if (fetchedExpense != null) {
                        expense = fetchedExpense
                        title = fetchedExpense.title ?: ""
                        description = fetchedExpense.description ?: ""
                        value = fetchedExpense.value.toString()
                        date = "" // Adicione conversão de data se necessário
                    }
                } else {
                    errorMessageData = "Documento não encontrado."
                }
                isLoadingData = false

                println("Documento: $document")
            }
            .addOnFailureListener { exception ->
                errorMessageData = "Erro ao buscar dados: ${exception.localizedMessage}"
                isLoadingData = false

                println("addOnFailureListener exception: $exception.localizedMessage")
            }
    }

    if (isLoadingData) {
        // Exibir indicador de carregamento
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessageData != null) {
        errorMessageData?.let {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        return
    }


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

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                // Room
                // viewModel.updateTransaction(
                    // expense.copy(
                        // title = title,
                        // description = description,
                        // value = value.toDoubleOrNull() ?: 0.0,
                        // date = System.currentTimeMillis()
                    // )
                // )
                // onNavigateBack()

                // Firebase
                if (title.isNotEmpty() && value.toDoubleOrNull() != null) {
                    isLoading = true
                    errorMessage = null

                    updateTransactionExpense(
                        transactionId = (expenseId ?: "").toString(),
                        updatedTransaction = expense!!.copy(
                            title = title,
                            description = description,
                            value = value.toDoubleOrNull() ?: 0.0,
                            date = parseDateToTimestamp(date) ?: 0L // Ajustar conversão de data se necessário
                        ),
                        onSuccess = {
                            isLoading = false
                            println("Transação atualizada com sucesso!")
                            onNavigateBack()
                        },
                        onFailure = { exception ->
                            isLoading = false
                            errorMessage = "Erro ao atualizar: ${exception.localizedMessage}"
                            println("Erro ao atualizar transação: $exception")
                        }
                    )
                } else {
                    errorMessage = "Preencha todos os campos corretamente."
                }
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

// Função para atualizar os dados no Firebase
fun updateTransactionExpense(
    transactionId: String,
    updatedTransaction: Transaction,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    if (transactionId.isEmpty()) {
        onFailure(Exception("ID da transação inválido."))
        return
    }

    FirebaseFirestore.getInstance().collection("transactions")
        .document(transactionId)
        .set(updatedTransaction)
        .addOnSuccessListener {
            println("Transação atualizada com sucesso: $transactionId")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            println("Erro ao atualizar transação: $exception")
            onFailure(exception)
        }
}