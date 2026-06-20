package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sciencelabs.aitutor.data.model.InventoryCategory
import com.sciencelabs.aitutor.data.model.InventoryItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onItemSelected: (InventoryItem) -> Unit = {}
) {
    val items by viewModel.items.collectAsState()
    var search by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lab Inventory") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(12.dp)) {

            // Search field
            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                    viewModel.setQuery(it.text)
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (search.text.isNotEmpty()) {
                        IconButton(onClick = { search = TextFieldValue(""); viewModel.setQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                placeholder = { Text("Search containers, tools, chemicals...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category filter chips
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InventoryChip(label = "Containers", selected = viewModel.isCategorySelected(InventoryCategory.CONTAINER)) { viewModel.toggleCategory(InventoryCategory.CONTAINER) }
                InventoryChip(label = "Tools", selected = viewModel.isCategorySelected(InventoryCategory.TOOL)) { viewModel.toggleCategory(InventoryCategory.TOOL) }
                InventoryChip(label = "Chemicals", selected = viewModel.isCategorySelected(InventoryCategory.CHEMICAL)) { viewModel.toggleCategory(InventoryCategory.CHEMICAL) }
                InventoryChip(label = "Biology", selected = viewModel.isCategorySelected(InventoryCategory.BIOLOGY)) { viewModel.toggleCategory(InventoryCategory.BIOLOGY) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { viewModel.clearFilters() }) { Text("Clear") }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Item list
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    InventoryCard(item = item, onClick = { onItemSelected(item) })
                }
            }
        }
    }
}

@Composable
private fun InventoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = Modifier.height(40.dp)
    )
}

@Composable
private fun InventoryCard(item: InventoryItem, onClick: () -> Unit) {
    val background = when (item.category) {
        InventoryCategory.CONTAINER -> MaterialTheme.colorScheme.primaryContainer
        InventoryCategory.TOOL -> MaterialTheme.colorScheme.secondaryContainer
        InventoryCategory.CHEMICAL -> MaterialTheme.colorScheme.tertiaryContainer
        InventoryCategory.BIOLOGY -> MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Category badge
            AssistChip(onClick = {}, label = { Text(item.category.name.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)) })
        }
    }
}
