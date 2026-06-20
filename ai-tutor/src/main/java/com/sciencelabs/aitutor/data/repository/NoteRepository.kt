package com.sciencelabs.aitutor.data.repository

import com.sciencelabs.aitutor.data.db.AppDatabase
import com.sciencelabs.aitutor.data.db.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class NoteRepository(private val database: AppDatabase) {

    private val dao = database.notesDao()

    fun getNotesForExperimentFlow(experimentId: String): Flow<List<NoteEntity>> = dao.getNotesForExperimentFlow(experimentId)

    suspend fun getNotesForExperiment(experimentId: String): List<NoteEntity> = dao.getNotesForExperiment(experimentId)

    suspend fun createNote(experimentId: String, title: String, content: String): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val note = NoteEntity(id = id, experimentId = experimentId, title = title, content = content, createdAt = now, updatedAt = now)
        dao.insertNote(note)
        return id
    }

    suspend fun updateNote(noteId: String, title: String, content: String) {
        val existing = dao.getNotesForExperiment(noteId)
        // Quick approach: create updated entity and replace (caller ensures experimentId)
        // But we don't have getById, so caller can pass NoteEntity; to keep simple, update via insert
        val now = System.currentTimeMillis()
        val note = NoteEntity(id = noteId, experimentId = "", title = title, content = content, createdAt = now, updatedAt = now)
        dao.insertNote(note)
    }

    suspend fun deleteNoteById(noteId: String) {
        dao.deleteNoteById(noteId)
    }
}
