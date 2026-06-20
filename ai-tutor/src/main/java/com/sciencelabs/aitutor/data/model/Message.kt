package com.sciencelabs.aitutor.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class MessageRole {
    object User : MessageRole()
    object Assistant : MessageRole()
    object System : MessageRole()
}

@Serializable
data class Message(
    val id: String,
    val role: String, // "user", "assistant", or "system"
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
