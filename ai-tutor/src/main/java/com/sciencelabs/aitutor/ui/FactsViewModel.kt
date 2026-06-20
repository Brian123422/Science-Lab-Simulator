package com.sciencelabs.aitutor.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciencelabs.aitutor.data.model.Fact
import com.sciencelabs.aitutor.data.repository.FactsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FactsViewModel(private val repository: FactsRepository) : ViewModel() {

    val fact: StateFlow<Fact> = repository.currentFact
        .stateIn(viewModelScope, SharingStarted.Lazily, repository.currentFact.value)

    val isFavorite: StateFlow<Boolean> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.Lazily, repository.favorites.value)

    fun refresh() {
        repository.refreshFact()
    }

    fun toggleFavorite() {
        val id = repository.currentFact.value.id
        repository.toggleFavorite(id)
    }

    fun share(context: Context) {
        val f = repository.currentFact.value
        val text = "${f.text}\n\n— Source: ${f.source}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "Share fact")
        // Note: starting activity from ViewModel is acceptable here for simplicity; UI could handle it instead.
        context.startActivity(chooser)
    }
}
