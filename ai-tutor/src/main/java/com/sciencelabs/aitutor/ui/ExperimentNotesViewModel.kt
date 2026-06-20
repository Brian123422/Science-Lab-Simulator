package com.sciencelabs.aitutor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciencelabs.aitutor.data.db.NoteEntity
import com.sciencelabs.aitutor.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExperimentNotesViewModel(private val repository: NoteRepository, private val experimentId: String) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repository.getNotesForExperimentFlow(experimentId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createNote(title: String, content: String, onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.createNote(experimentId, title, content)
            onComplete(id)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.getNotesForExperiment(experimentId) // ensure loaded
            // perform update by inserting the new version
            repository.database.notesDao().insertNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNoteById(noteId)
        }
    }
}
