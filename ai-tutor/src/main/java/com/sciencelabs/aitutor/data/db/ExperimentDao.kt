package com.sciencelabs.aitutor.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Experiments and Messages.
 * Provides full CRUD operations for managing experiments.
 */
@Dao
interface ExperimentDao {

    // ===== EXPERIMENT OPERATIONS =====

    /**
     * Get all experiments, ordered by most recently updated first.
     * Excludes archived experiments by default.
     */
    @Query("SELECT * FROM experiments WHERE is_archived = 0 ORDER BY updated_at DESC")
    fun getAllExperiments(): Flow<List<ExperimentEntity>>

    /**
     * Get all experiments including archived ones.
     */
    @Query("SELECT * FROM experiments ORDER BY updated_at DESC")
    fun getAllExperimentsIncludingArchived(): Flow<List<ExperimentEntity>>

    /**
     * Get a single experiment by ID.
     */
    @Query("SELECT * FROM experiments WHERE id = :experimentId LIMIT 1")
    suspend fun getExperimentById(experimentId: String): ExperimentEntity?

    /**
     * Get experiments by topic.
     */
    @Query("SELECT * FROM experiments WHERE topic = :topic AND is_archived = 0 ORDER BY updated_at DESC")
    fun getExperimentsByTopic(topic: String): Flow<List<ExperimentEntity>>

    /**
     * Create (insert) a new experiment.
     */
    @Insert
    suspend fun insertExperiment(experiment: ExperimentEntity)

    /**
     * Update an existing experiment (e.g., rename, change description, update timestamp).
     */
    @Update
    suspend fun updateExperiment(experiment: ExperimentEntity)

    /**
     * Delete an experiment by ID (also cascades to messages due to foreign key constraint).
     */
    @Query("DELETE FROM experiments WHERE id = :experimentId")
    suspend fun deleteExperimentById(experimentId: String)

    /**
     * Archive an experiment (soft delete).
     */
    @Query("UPDATE experiments SET is_archived = 1, updated_at = :timestamp WHERE id = :experimentId")
    suspend fun archiveExperiment(experimentId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Unarchive an experiment.
     */
    @Query("UPDATE experiments SET is_archived = 0, updated_at = :timestamp WHERE id = :experimentId")
    suspend fun unarchiveExperiment(experimentId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Search experiments by title (partial match).
     */
    @Query("SELECT * FROM experiments WHERE title LIKE :query AND is_archived = 0 ORDER BY updated_at DESC")
    fun searchExperiments(query: String): Flow<List<ExperimentEntity>>

    // ===== MESSAGE OPERATIONS =====

    /**
     * Get all messages for an experiment, ordered by creation time.
     */
    @Query("SELECT * FROM messages WHERE experiment_id = :experimentId ORDER BY created_at ASC")
    suspend fun getMessagesForExperiment(experimentId: String): List<MessageEntity>

    /**
     * Get all messages for an experiment as a Flow (for reactive UI updates).
     */
    @Query("SELECT * FROM messages WHERE experiment_id = :experimentId ORDER BY created_at ASC")
    fun getMessagesForExperimentFlow(experimentId: String): Flow<List<MessageEntity>>

    /**
     * Insert a single message.
     */
    @Insert
    suspend fun insertMessage(message: MessageEntity)

    /**
     * Insert multiple messages.
     */
    @Insert
    suspend fun insertMessages(messages: List<MessageEntity>)

    /**
     * Update a message.
     */
    @Update
    suspend fun updateMessage(message: MessageEntity)

    /**
     * Delete a message by ID.
     */
    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    /**
     * Delete all messages for an experiment.
     */
    @Query("DELETE FROM messages WHERE experiment_id = :experimentId")
    suspend fun deleteMessagesForExperiment(experimentId: String)

    /**
     * Get the count of messages in an experiment.
     */
    @Query("SELECT COUNT(*) FROM messages WHERE experiment_id = :experimentId")
    suspend fun getMessageCount(experimentId: String): Int

    // ===== TRANSACTION OPERATIONS (Complex workflows) =====

    /**
     * Duplicate an experiment: creates a new experiment with the same messages.
     * The new experiment gets a new ID and "(Copy)" suffix in the title.
     */
    @Transaction
    suspend fun duplicateExperiment(sourceExperimentId: String): String {
        val source = getExperimentById(sourceExperimentId) ?: return ""
        val newId = java.util.UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val newExperiment = source.copy(
            id = newId,
            title = "${source.title} (Copy)",
            createdAt = now,
            updatedAt = now,
            isArchived = false
        )
        insertExperiment(newExperiment)
        val messages = getMessagesForExperiment(sourceExperimentId)
        val newMessages = messages.map { it.copy(id = java.util.UUID.randomUUID().toString(), experimentId = newId) }
        if (newMessages.isNotEmpty()) {
            insertMessages(newMessages)
        }
        return newId
    }

    /**
     * Rename an experiment.
     */
    suspend fun renameExperiment(experimentId: String, newTitle: String) {
        val experiment = getExperimentById(experimentId) ?: return
        updateExperiment(experiment.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    /**
     * Get the total count of non-archived experiments.
     */
    @Query("SELECT COUNT(*) FROM experiments WHERE is_archived = 0")
    suspend fun getExperimentCount(): Int
}
