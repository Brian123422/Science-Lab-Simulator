package com.sciencelabs.aitutor.ui

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sciencelabs.aitutor.data.db.AppDatabase
import com.sciencelabs.aitutor.data.repository.ExperimentRepository

@Composable
fun TutorNavHost(startDestination: String = "history") {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ExperimentRepository(database) }

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
    }
}
