package com.example.wallet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.wallet.R
import com.example.wallet.data.repositories.UserRepository
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.theme.BlueBright
import com.example.wallet.ui.theme.Gray600
import com.example.wallet.ui.theme.GreenBase
import com.example.wallet.ui.theme.GreenLight
import com.example.wallet.ui.theme.RedBase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

data class Transaction(
    var id: String? = null,
    val userId: Int = 0,
    val title: String = "",
    val description: String = "",
    val value: Double = 0.0,
    val date: Long = 0L,
    val type: String = "" // "despesa" ou "ganho"
) {
    // Construtor sem argumentos
    constructor() : this(
        id = null,
        userId = 0,
        title = "",
        description = "",
        value = 0.0,
        date = 0L,
        type = ""
    )
}

@Composable
fun HomeScreen(
    userRepository: UserRepository,
    transactionViewModel: TransactionViewModel,
    onNavigateToDespesas: () -> Unit,
    onNavigateToGanhos: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }

    // Room
    // val totalGains by transactionViewModel.totalGains.collectAsState()
    // val totalExpenses by transactionViewModel.totalExpenses.collectAsState()
    // val currentBalance by transactionViewModel.currentBalance.collectAsState()

    // Firebase
    var totalGains by remember { mutableStateOf(0.0) }
    var totalExpenses by remember { mutableStateOf(0.0) }
    var currentBalance by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Recuperar o usuário corrente e os dados financeiros ao iniciar a tela
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                userName = currentUser?.name ?: "Usuário Desconhecido"

                val userId = currentUser?.id ?: return@launch

                transactionViewModel.fetchTotalExpenses(userId)
                transactionViewModel.fetchTotalGains(userId)

                FirebaseFirestore.getInstance()
                    .collection("transactions")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val transactions = documents.mapNotNull { it.toObject(Transaction::class.java) }
                        totalGains = transactions.filter { it.type == "ganho" }.sumOf { it.value }
                        totalExpenses = transactions.filter { it.type == "despesa" }.sumOf { it.value }
                        currentBalance = totalGains - totalExpenses
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = "Erro ao carregar dados: ${exception.localizedMessage}"
                        isLoading = false
                    }
            } catch (e: Exception) {
                errorMessage = "Erro inesperado: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    if (isLoading) {
        // Indicador de carregamento
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessage != null) {
        // Mensagem de erro
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage ?: "Erro desconhecido.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(color = Gray600)
            .padding(top = 26.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bem-vindo e Nome do usuário
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Bem-vindo e Nome do usuário com botão de logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texto de boas-vindas e nome do usuário
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Bem-vindo", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = userName, fontSize = 20.sp, color = Color.White)
                }

                // Botão de Logout com imagem
                Button(
                    onClick = onLogout,
                    modifier = Modifier.size(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logout), // Substitua com o ícone de logout ou a imagem desejada
                        contentDescription = "Logout",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Total de Ganhos e Total de Despesas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(RedBase, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_upward),
                    contentDescription = "Balance Icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = "Total de Ganhos", color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "AOA %.2f".format(totalGains), color = Color.White, fontSize = 18.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(BlueBright, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_downward),
                    contentDescription = "Balance Icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = "Total de Despesas", color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "AOA %.2f".format(totalExpenses), color = Color.White, fontSize = 18.sp)
            }
        }

        // Saldo Atual
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenBase, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.account_balance_wallet),
                contentDescription = "Balance Icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = "Saldo Atual", color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "AOA %.2f".format(currentBalance), color = Color.White, fontSize = 18.sp)
        }

        // Botão: Gerenciamento de Despesas
        Button(
            onClick = onNavigateToDespesas,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = "Gerenciamento de Despesas", color = Color.White)
        }

        // Botão: Gerenciamento de Ganhos
        Button(
            onClick = onNavigateToGanhos,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
        ) {
            Text(text = "Gerenciamento de Ganhos", color = Color.White)
        }
    }
}
