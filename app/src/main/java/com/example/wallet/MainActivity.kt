package com.example.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallet.data.database.AppDatabase
import com.example.wallet.data.repositories.TransactionRepository
import com.example.wallet.data.repositories.UserRepository
import com.example.wallet.data.viewmodel.TransactionViewModel
import com.example.wallet.ui.screen.HomeScreen
import com.example.wallet.ui.screen.LoginScreen
import com.example.wallet.ui.screen.CadastroScreen
import com.example.wallet.ui.screen.DespesasCriarScreen
import com.example.wallet.ui.screen.DespesasEditarScreen
import com.example.wallet.ui.screen.DespesasScreen
import com.example.wallet.ui.screen.GanhosCriarScreen
import com.example.wallet.ui.screen.GanhosEditarScreen
import com.example.wallet.ui.screen.GanhosScreen
import com.example.wallet.ui.screen.SplashScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        lifecycleScope.launch {
            // Verifica se existe um usuário logado
            val database = AppDatabase.getDatabase(applicationContext)
            val userRepository = UserRepository(
                userDao = database.userDao(),
                currentUserDao = database.currentUserDao()
            )

            val transactionRepository = TransactionRepository(database.transactionDao())
            val transactionViewModel = TransactionViewModel(transactionRepository)

            val currentUser = userRepository.getCurrentUser()
            val currentUserId = currentUser?.id ?: -1 // Define -1 se não houver usuário

            setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(
                        isLoggedIn = currentUser != null,
                        onNavigateToLogin = {
                            navController.navigate("login")
                        },
                        onNavigateToHome = {
                            navController.navigate("home")
                        }
                    )
                }
                // Tela de login
                composable("login") {
                    LoginScreen(
                        userRepository = userRepository,
                        onNavigateToCadastro = {
                            navController.navigate("cadastro")
                        }, onNavigateToHome = {
                            navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    })
                }

                // Tela de cadastro
                composable("cadastro") {
                    CadastroScreen(
                        userRepository = userRepository,
                        onNavigateBack = {
                        navController.popBackStack()
                    })
                }

                // Tela inicial
                composable("home") {
                    HomeScreen(
                        userRepository = userRepository,
                        transactionViewModel = transactionViewModel,
                        onNavigateToDespesas = {
                            navController.navigate("despesas")
                        },
                        onNavigateToGanhos = {
                            navController.navigate("ganhos")
                        },
                        onLogout = {
                            lifecycleScope.launch {
                                userRepository.logoutCurrentUser()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                // Tela de despesas
                composable("despesas") {
                    DespesasScreen(
                        viewModel = transactionViewModel, // Passe seu TransactionViewModel aqui
                        userId = currentUserId,          // Substitua por sua lógica para obter o ID do usuário
                        onCreateExpense = {
                            navController.navigate("despesas/criar")
                        },
                        onEditExpense = { transaction ->
                            navController.navigate("despesas/editar/${transaction.id}")
                        }
                    )
                }

                // Tela de criação de despesa
                composable("despesas/criar") {
                    DespesasCriarScreen (
                        viewModel = transactionViewModel,
                        userId = currentUserId,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // Tela de edição de despesa
                composable(
                    route = "despesas/editar/{expenseId}",
                    arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: -1

                    // Obtenha a transação correspondente do ViewModel
                    val expense = transactionViewModel.expenses.value.find { it.id == expenseId }

                    if (expense != null) {
                        DespesasEditarScreen(
                            viewModel = transactionViewModel,
                            expense = expense,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    } else {
                        // Tratamento para quando a transação não é encontrada
                        Text("Transação não encontrada!")
                    }
                }

                composable("ganhos") {
                    GanhosScreen(
                        viewModel = transactionViewModel,
                        userId = currentUserId,
                        onCreateIncome = {
                            navController.navigate("ganhos/criar")
                        },
                        onEditIncome = { transaction ->
                            navController.navigate("ganhos/editar/${transaction.id}")
                        }
                    )
                }

                composable("ganhos/criar") {
                    GanhosCriarScreen(
                        viewModel = transactionViewModel,
                        userId = currentUserId,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = "ganhos/editar/{incomeId}",
                    arguments = listOf(navArgument("incomeId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val incomeId = backStackEntry.arguments?.getInt("incomeId") ?: -1
                    val income = transactionViewModel.incomes.value.find { it.id == incomeId }

                    if (income != null) {
                        GanhosEditarScreen(
                            viewModel = transactionViewModel,
                            income = income,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    } else {
                        Text("Ganho não encontrado!")
                    }
                }
            }
        }
        }
    }
}

