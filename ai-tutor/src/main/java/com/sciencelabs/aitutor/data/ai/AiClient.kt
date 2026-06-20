package com.sciencelabs.aitutor.data.ai

import com.sciencelabs.aitutor.data.model.Message

/**
 * An abstraction over an AI provider.
 * Implementations should stream partial tokens via onDelta where possible.
 */
interface AiClient {
    suspend fun sendMessage(
        messages: List<Message>,
        onDelta: (String) -> Unit = {}
    ): Message
}
