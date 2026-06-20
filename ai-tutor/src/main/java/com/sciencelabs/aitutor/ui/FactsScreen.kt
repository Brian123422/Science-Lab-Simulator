package com.sciencelabs.aitutor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sciencelabs.aitutor.data.repository.FactsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactsScreen(viewModel: FactsViewModel) {
    val fact by viewModel.fact.collectAsState()
    val favorites by viewModel.isFavorite.collectAsState()
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Science Facts") }) }) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize(), contentAlignment = Alignment.TopCenter) {

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = fact.text, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Source: ${fact.source}", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }

                        IconButton(onClick = {
                            viewModel.toggleFavorite()
                        }) {
                            if (favorites) Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Unfavorite")
                            else Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "Favorite")
                        }

                        Button(onClick = { viewModel.share(context) }) {
                            Text("Share")
                        }
                    }
                }
            }
        }
    }
}
