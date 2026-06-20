package com.sciencelabs.aitutor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciencelabs.aitutor.data.db.ExperimentEntity
import com.sciencelabs.aitutor.data.repository.ExperimentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExperimentListViewModel(
    private val repository: ExperimentRepository
) : ViewModel() {

    // Expose experiments as a StateFlow for Compose to collect. We map to ExperimentEntity list directly.
    val experiments: StateFlow<List<ExperimentEntity>> = repository.getAllExperiments()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun renameExperiment(experimentId: String, newTitle: String) {
        viewModelScope.launch {
            repository.renameExperiment(experimentId, newTitle)
        }
    }

    fun duplicateExperiment(experimentId: String): String {
        var newId = ""
        viewModelScope.launch {
            newId = repository.duplicateExperiment(experimentId)
        }
        return newId
    }

    fun deleteExperiment(experimentId: String) {
        viewModelScope.launch {
            repository.deleteExperiment(experimentId)
        }
    }

    fun openExperiment(experimentId: String) {
        // Navigation handled by the UI layer via callback; keep this here for symmetry.
    }
}
