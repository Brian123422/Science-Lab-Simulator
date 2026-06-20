package com.sciencelabs.aitutor.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents an experiment (conversation session) in the tutor.
 * Each experiment contains a sequence of messages (Q&A pairs).
 */
@Entity(tableName = "experiments")
data class ExperimentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "topic") val topic: String = "", // e.g., "Biology", "Chemistry", "Physics"
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false
)

/**
 * Represents a single message (question or answer) in an experiment.
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ExperimentEntity::class,
            parentColumns = ["id"],
            childColumns = ["experiment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "experiment_id") val experimentId: String,
    @ColumnInfo(name = "role") val role: String, // "user" or "assistant"
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
