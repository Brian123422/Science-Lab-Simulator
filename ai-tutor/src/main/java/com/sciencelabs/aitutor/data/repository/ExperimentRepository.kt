package com.sciencelabs.aitutor.data.repository

import com.sciencelabs.aitutor.data.db.AppDatabase
import com.sciencelabs.aitutor.data.db.ExperimentEntity
import com.sciencelabs.aitutor.data.db.MessageEntity
import com.sciencelabs.aitutor.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Repository for managing experiments.
 * Provides a clean API over the Room DAO for the rest of the app.
 */
class ExperimentRepository(private val database: AppDatabase) {

    private val dao = database.experimentDao()

    // ===== READ OPERATIONS =====

    /**
     * Get all active (non-archived) experiments as a Flow.
     */
    fun getAllExperiments(): Flow<List<ExperimentEntity>> = dao.getAllExperiments()

    /**
     * Get all experiments including archived ones.
     */
    fun getAllExperimentsIncludingArchived(): Flow<List<ExperimentEntity>> =
        dao.getAllExperimentsIncludingArchived()

    /**
     * Get a single experiment by ID.
     */
    suspend fun getExperimentById(experimentId: String): ExperimentEntity? =
        dao.getExperimentById(experimentId)

    /**
     * Get experiments by topic.
     */
    fun getExperimentsByTopic(topic: String): Flow<List<ExperimentEntity>> =
        dao.getExperimentsByTopic(topic)

    /**
     * Search experiments by title.
     */
    fun searchExperiments(query: String): Flow<List<ExperimentEntity>> =
        dao.searchExperiments("%$query%")

    /**
     * Get the count of all non-archived experiments.
     */
    suspend fun getExperimentCount(): Int = dao.getExperimentCount()

    /**
     * Get all messages for an experiment.
     */
    suspend fun getMessagesForExperiment(experimentId: String): List<Message> {
        return dao.getMessagesForExperiment(experimentId).map { it.toMessage() }
    }

    /**
     * Get all messages for an experiment as a Flow.
     */
    fun getMessagesForExperimentFlow(experimentId: String): Flow<List<Message>> =
        dao.getMessagesForExperimentFlow(experimentId).map { messages ->
            messages.map { it.toMessage() }
        }

    /**
     * Get the message count for an experiment.
     */
    suspend fun getMessageCount(experimentId: String): Int =
        dao.getMessageCount(experimentId)

    // ===== CREATE OPERATIONS =====

    /**
     * Create a new experiment.
     */
    suspend fun createExperiment(
        title: String,
        description: String = "",
        topic: String = ""
    ): String {
        val experimentId = UUID.randomUUID().toString()
        val experiment = ExperimentEntity(
            id = experimentId,
            title = title,
            description = description,
            topic = topic
        )
        dao.insertExperiment(experiment)
        return experimentId
    }

    /**
     * Add a message to an experiment.
     */
    suspend fun addMessage(experimentId: String, role: String, text: String) {
        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            experimentId = experimentId,
            role = role,
            text = text
        )
        dao.insertMessage(message)
        // Update the experiment's updatedAt timestamp
        val experiment = dao.getExperimentById(experimentId) ?: return
        dao.updateExperiment(experiment.copy(updatedAt = System.currentTimeMillis()))
    }

    /**
     * Add multiple messages to an experiment in a single batch.
     */
    suspend fun addMessages(experimentId: String, messages: List<Message>) {
        val entities = messages.map { msg ->
            MessageEntity(
                id = UUID.randomUUID().toString(),
                experimentId = experimentId,
                role = msg.role,
                text = msg.text
            )
        }
        if (entities.isNotEmpty()) {
            dao.insertMessages(entities)
            // Update the experiment's updatedAt timestamp
            val experiment = dao.getExperimentById(experimentId) ?: return
            dao.updateExperiment(experiment.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    // ===== UPDATE OPERATIONS =====

    /**
     * Rename an experiment.
     */
    suspend fun renameExperiment(experimentId: String, newTitle: String) {
        dao.renameExperiment(experimentId, newTitle)
    }

    /**
     * Update the description of an experiment.
     */
    suspend fun updateExperimentDescription(experimentId: String, newDescription: String) {
        val experiment = dao.getExperimentById(experimentId) ?: return
        dao.updateExperiment(
            experiment.copy(
                description = newDescription,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Update the topic of an experiment.
     */
    suspend fun updateExperimentTopic(experimentId: String, newTopic: String) {
        val experiment = dao.getExperimentById(experimentId) ?: return
        dao.updateExperiment(
            experiment.copy(
                topic = newTopic,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // ===== DELETE OPERATIONS =====

    /**
     * Delete an experiment permanently (including all its messages).
     */
    suspend fun deleteExperiment(experimentId: String) {
        dao.deleteExperimentById(experimentId)
    }

    /**
     * Archive an experiment (soft delete).
     */
    suspend fun archiveExperiment(experimentId: String) {
        dao.archiveExperiment(experimentId)
    }

    /**
     * Unarchive an experiment.
     */
    suspend fun unarchiveExperiment(experimentId: String) {
        dao.unarchiveExperiment(experimentId)
    }

    /**
     * Delete all messages in an experiment (keeping the experiment itself).
     */
    suspend fun clearExperimentMessages(experimentId: String) {
        dao.deleteMessagesForExperiment(experimentId)
        // Update the experiment's updatedAt timestamp
        val experiment = dao.getExperimentById(experimentId) ?: return
        dao.updateExperiment(experiment.copy(updatedAt = System.currentTimeMillis()))
    }

    // ===== DUPLICATE OPERATION =====

    /**
     * Duplicate an experiment: creates a new experiment with all the same messages.
     * Returns the ID of the new experiment.
     */
    suspend fun duplicateExperiment(sourceExperimentId: String): String {
        return dao.duplicateExperiment(sourceExperimentId)
    }

    // ===== HELPER EXTENSIONS =====

    private fun MessageEntity.toMessage(): Message = Message(
        id = id,
        role = role,
        text = text,
        createdAt = createdAt
    )
}
