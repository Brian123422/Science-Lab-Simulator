package com.sciencelabs.aitutor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciencelabs.aitutor.data.ai.AiClient
import com.sciencelabs.aitutor.data.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatUiState(
    val messages: List<Message> = emptyList()
)

class ChatViewModel(
    private val aiClient: AiClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadConversation(messages: List<Message>) {
        _uiState.value = ChatUiState(messages = messages)
    }

    suspend fun sendMessage(userMessage: Message) {
        // Append user message
        val current = _uiState.value.messages.toMutableList()
        current.add(userMessage)
        _uiState.value = ChatUiState(messages = current)

        viewModelScope.launch {
            // Call AI client and stream or collect result
            val assistant = aiClient.sendMessage(current) { delta ->
                // For streaming: append or update last assistant partial text
                // Simple approach: when delta arrives, replace or append a provisional assistant message
                val msgs = _uiState.value.messages.toMutableList()
                // If last message is a provisional assistant message, update it, else add one
                if (msgs.lastOrNull()?.role == "assistant") {
                    val last = msgs.last().copy(text = msgs.last().text + delta)
                    msgs[msgs.lastIndex] = last
                } else {
                    msgs.add(Message(id = java.util.UUID.randomUUID().toString(), role = "assistant", text = delta))
                }
                _uiState.value = ChatUiState(messages = msgs)
            }

            // Replace provisional assistant message with final assistant message
            val finalMsgs = _uiState.value.messages.toMutableList()
            if (finalMsgs.lastOrNull()?.role == "assistant") {
                finalMsgs[finalMsgs.lastIndex] = assistant
            } else {
                finalMsgs.add(assistant)
            }
            _uiState.value = ChatUiState(messages = finalMsgs)
        }
    }

    // convenience for Compose coroutine scope
    suspend fun sendMessage(messageText: Message) = sendMessage(messageText)
}
