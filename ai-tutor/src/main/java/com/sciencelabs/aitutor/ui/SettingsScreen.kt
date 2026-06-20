package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        TopAppBar(title = { Text("AI Tutor Settings") })
        OutlinedTextField(value = apiKey, onValueChange = onApiKeyChange, label = { Text("API Key") })
        Button(onClick = onSave, modifier = Modifier.padding(top = 12.dp)) {
            Text("Save")
        }
    }
}
