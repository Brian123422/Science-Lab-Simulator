package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sciencelabs.aitutor.data.db.NoteEntity
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentNotesScreen(
    viewModel: ExperimentNotesViewModel,
    onBack: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<NoteEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf<NoteEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Experiment Notes") },
                navigationIcon = null,
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { note ->
                NoteCard(note = note, onEdit = { editTarget = it }, onDelete = { showDeleteDialog = it })
            }
        }

        if (showCreateDialog) {
            NoteEditDialog(title = "New Note", initialTitle = "", initialContent = "", onCancel = { showCreateDialog = false }, onSave = { t, c ->
                scope.launch {
                    viewModel.createNote(t, c) { _ -> showCreateDialog = false }
                }
            })
        }

        if (editTarget != null) {
            val n = editTarget!!
            NoteEditDialog(title = "Edit Note", initialTitle = n.title, initialContent = n.content, onCancel = { editTarget = null }, onSave = { t, c ->
                scope.launch {
                    viewModel.updateNote(n.copy(title = t, content = c, updatedAt = System.currentTimeMillis()))
                    editTarget = null
                }
            })
        }

        if (showDeleteDialog != null) {
            val n = showDeleteDialog!!
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Note") },
                text = { Text("Delete '${n.title}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            viewModel.deleteNote(n.id)
                            showDeleteDialog = null
                        }
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun NoteCard(note: NoteEntity, onEdit: (NoteEntity) -> Unit, onDelete: (NoteEntity) -> Unit) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault()) }
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onEdit(note) }) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Updated: ${formatter.format(Instant.ofEpochMilli(note.updatedAt))}", style = MaterialTheme.typography.bodySmall)
                Row {
                    IconButton(onClick = { onEdit(note) }) { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit") }
                    IconButton(onClick = { onDelete(note) }) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete") }
                }
            }
        }
    }
}

@Composable
private fun NoteEditDialog(
    title: String,
    initialTitle: String,
    initialContent: String,
    onCancel: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var t by remember { mutableStateOf(TextFieldValue(initialTitle)) }
    var c by remember { mutableStateOf(TextFieldValue(initialContent)) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("Title") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = c, onValueChange = { c = it }, label = { Text("Content") }, modifier = Modifier.height(150.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(t.text.trim(), c.text.trim()) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}
