package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sciencelabs.aitutor.data.db.ExperimentEntity
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentHistoryScreen(
    viewModel: ExperimentListViewModel,
    onOpenExperiment: (String) -> Unit
) {
    val experiments by viewModel.experiments.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var renameTarget by remember { mutableStateOf<ExperimentEntity?>(null) }
    var renameText by remember { mutableStateOf("") }
    var showRenameDialog by remember { mutableStateOf(false) }

    var deleteTarget by remember { mutableStateOf<ExperimentEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Experiment History") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            items(experiments) { exp ->
                ExperimentRow(
                    experiment = exp,
                    onOpen = { onOpenExperiment(exp.id) },
                    onRename = {
                        renameTarget = exp
                        renameText = exp.title
                        showRenameDialog = true
                    },
                    onDuplicate = {
                        scope.launch {
                            val newId = viewModel.duplicateExperiment(exp.id)
                            snackbarHostState.showSnackbar("Duplicated '${exp.title}'")
                            // Optionally navigate to new experiment
                        }
                    },
                    onDelete = {
                        deleteTarget = exp
                        showDeleteDialog = true
                    }
                )
                Divider()
            }
        }

        if (showRenameDialog && renameTarget != null) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Rename Experiment") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = renameText,
                            onValueChange = { renameText = it },
                            label = { Text("Title") },
                            singleLine = true
                        )
                        Text("Category: ${renameTarget?.topic ?: "Uncategorized"}", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            renameTarget?.let { viewModel.renameExperiment(it.id, renameText) }
                            showRenameDialog = false
                            snackbarHostState.showSnackbar("Renamed to '$renameText'")
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
                }
            )
        }

        if (showDeleteDialog && deleteTarget != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Experiment") },
                text = { Text("Delete '${deleteTarget?.title}'? This will permanently remove all messages.") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            deleteTarget?.let { viewModel.deleteExperiment(it.id) }
                            showDeleteDialog = false
                            snackbarHostState.showSnackbar("Deleted '${deleteTarget?.title}'")
                        }
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun ExperimentRow(
    experiment: ExperimentEntity,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())
    }
    var menuExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpen() }
        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = experiment.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Created: ${formatter.format(Instant.ofEpochMilli(experiment.createdAt))}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Category: ${experiment.topic.ifBlank { "Uncategorized" }}", style = MaterialTheme.typography.bodySmall)
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(text = { Text("Open") }, onClick = { menuExpanded = false; onOpen() })
                DropdownMenuItem(text = { Text("Rename") }, onClick = { menuExpanded = false; onRename() })
                DropdownMenuItem(text = { Text("Duplicate") }, onClick = { menuExpanded = false; onDuplicate() })
                DropdownMenuItem(text = { Text("Delete", color = MaterialTheme.colorScheme.error) }, onClick = { menuExpanded = false; onDelete() })
            }
        }
    }
}
