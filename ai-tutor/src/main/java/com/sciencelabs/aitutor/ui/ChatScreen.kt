package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sciencelabs.aitutor.data.model.Message
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var input by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Science Tutor") })
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {

                LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                    items(uiState.messages) { msg ->
                        MessageRow(msg)
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask a question about biology, chemistry, physics...") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        val text = input.text.trim()
                        if (text.isNotEmpty()) {
                            val message = Message(
                                id = UUID.randomUUID().toString(),
                                role = "user",
                                text = text
                            )
                            coroutineScope.launch { viewModel.sendMessage(message) }
                            input = TextFieldValue("")
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    )
}

@Composable
private fun MessageRow(message: Message) {
    val isUser = message.role == "user"
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 720.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(message.text, color = if (isUser) Color.White else Color.Unspecified)
            }
        }
    }
}
