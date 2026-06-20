package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sciencelabs.aitutor.data.db.AppDatabase
import com.sciencelabs.aitutor.data.repository.ExperimentRepository
import com.sciencelabs.aitutor.data.repository.InventoryRepository
import com.sciencelabs.aitutor.data.repository.FactsRepository
import com.sciencelabs.aitutor.data.repository.NoteRepository
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun TutorNavHost(startDestination: String = "history") {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ExperimentRepository(database) }
    val inventoryRepo = remember { InventoryRepository() }
    val factsRepo = remember { FactsRepository() }
    val noteRepo = remember { NoteRepository(database) }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("history") {
            val viewModel = remember { ExperimentListViewModel(repository) }
            ExperimentHistoryScreen(viewModel = viewModel, onOpenExperiment = { id ->
                navController.navigate("chat/$id")
            })
        }

        composable("chat/{experimentId}") { backStackEntry ->
            val experimentId = backStackEntry.arguments?.getString("experimentId") ?: return@composable
            val chatViewModel = remember { ExperimentChatViewModelFactory(repository, experimentId).create(ExperimentChatViewModel::class.java) }
            ExperimentChatScreen(experimentId = experimentId, repository = repository, onBack = { navController.popBackStack() }, onOpenNotes = { navController.navigate("notes/$experimentId") })
        }

        composable("inventory") {
            val viewModel = remember { InventoryViewModel(inventoryRepo) }
            InventoryScreen(viewModel = viewModel, onItemSelected = {
                // Simple behavior: show a snackbar or extend navigation as needed
            })
        }

        composable("facts") {
            val viewModel = remember { FactsViewModel(factsRepo) }
            FactsScreen(viewModel = viewModel)
        }

        composable("notes/{experimentId}") { backStackEntry ->
            val experimentId = backStackEntry.arguments?.getString("experimentId") ?: return@composable
            val viewModel = remember { ExperimentNotesViewModel(NoteRepository(database), experimentId) }
            ExperimentNotesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
