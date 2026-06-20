package com.briantech.sciencelabsimulator.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.briantech.sciencelabsimulator.ui.screens.ChemistryScreen
import com.briantech.sciencelabsimulator.ui.screens.ChemistryDetailScreen
import com.briantech.sciencelabsimulator.ui.screens.PhysicsScreen
import com.briantech.sciencelabsimulator.ui.screens.BiologyScreen
import com.briantech.sciencelabsimulator.ui.screens.AiTutorScreen
import com.briantech.sciencelabsimulator.ui.screens.SavedExperimentsScreen
import com.briantech.sciencelabsimulator.ui.screens.HomeScreen
import com.briantech.sciencelabsimulator.ui.viewmodel.ChemistryLabViewModel

@Composable
fun ScienceLabApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        val chemistryLabViewModel: ChemistryLabViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("chemistry") {
                ChemistryScreen(navController = navController)
            }
            composable("chemistry_detail") {
                ChemistryDetailScreen(navController = navController, viewModel = chemistryLabViewModel)
            }
            composable("physics") {
                PhysicsScreen(navController = navController)
            }
            composable("biology") {
                BiologyScreen(navController = navController)
            }
            composable("ai_tutor") {
                AiTutorScreen(navController = navController)
            }
            composable("saved_experiments") {
                SavedExperimentsScreen(navController = navController)
            }
        }
    }
}
