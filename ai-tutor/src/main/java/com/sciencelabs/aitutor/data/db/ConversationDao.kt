package com.sciencelabs.aitutor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConversationDao {
    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    suspend fun getMessagesForConversation(conversationId: String): List<MessageEntity>

    @Insert
    suspend fun insertMessage(msg: MessageEntity)

    @Insert
    suspend fun insertConversation(conv: ConversationEntity)
}
