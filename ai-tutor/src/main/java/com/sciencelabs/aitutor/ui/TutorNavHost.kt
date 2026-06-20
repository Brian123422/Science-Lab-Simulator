package com.sciencelabs.aitutor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sciencelabs.aitutor.data.db.AppDatabase
import com.sciencelabs.aitutor.data.repository.ExperimentRepository
import com.sciencelabs.aitutor.data.repository.InventoryRepository

@Composable
fun TutorNavHost(startDestination: String = "history") {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ExperimentRepository(database) }
    val inventoryRepo = remember { InventoryRepository() }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("history") {
            val viewModel = remember { ExperimentListViewModel(repository) }
            ExperimentHistoryScreen(viewModel = viewModel, onOpenExperiment = { id ->
                navController.navigate("chat/$id")
            })
        }

        composable("chat/{experimentId}") { backStackEntry ->
            val experimentId = backStackEntry.arguments?.getString("experimentId") ?: return@composable
            ExperimentChatScreen(experimentId = experimentId, repository = repository, onBack = { navController.popBackStack() })
        }

        composable("inventory") {
            val viewModel = remember { InventoryViewModel(inventoryRepo) }
            InventoryScreen(viewModel = viewModel, onItemSelected = {
                // Simple behavior: show a snackbar or extend navigation as needed
            })
        }
    }
}
