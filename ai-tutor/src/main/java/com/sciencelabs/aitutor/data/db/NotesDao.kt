package com.sciencelabs.aitutor.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes WHERE experiment_id = :experimentId ORDER BY updated_at DESC")
    fun getNotesForExperimentFlow(experimentId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE experiment_id = :experimentId ORDER BY updated_at DESC")
    suspend fun getNotesForExperiment(experimentId: String): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("DELETE FROM notes WHERE experiment_id = :experimentId")
    suspend fun deleteNotesForExperiment(experimentId: String)
}
