package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sciencelabs.aitutor.data.model.Message
import com.sciencelabs.aitutor.data.repository.ExperimentRepository
import kotlinx.coroutines.flow.Flow

@Composable
fun ExperimentChatScreen(
    experimentId: String,
    repository: ExperimentRepository,
    onBack: () -> Unit
) {
    val messagesFlow: Flow<List<Message>> = repository.getMessagesForExperimentFlow(experimentId)
    val messages by messagesFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Experiment") }, navigationIcon = null)
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()) {
            items(messages) { msg ->
                MessageRow(msg)
                Divider()
            }
        }
    }
}
