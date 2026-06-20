package com.briantech.sciencelabsimulator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.briantech.sciencelabsimulator.R
import com.briantech.sciencelabsimulator.data.model.*
import com.briantech.sciencelabsimulator.ui.components.ContainerVisualization
import com.briantech.sciencelabsimulator.ui.viewmodel.ChemistryLabViewModel

@Composable
fun ChemistryDetailScreen(
    navController: NavController,
    viewModel: ChemistryLabViewModel = viewModel()
) {
    val containers by viewModel.containers.collectAsState()
    val selectedContainer by viewModel.selectedContainer.collectAsState()
    val reactionActive by viewModel.reactionActive.collectAsState()
    val reactionLog by viewModel.reactionLog.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val pouringFromId by viewModel.pouringFromId.collectAsState()
    val pouringToId by viewModel.pouringToId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Chemistry Lab",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        // Main Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Lab Workspace (Left)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chemistry Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Containers Grid
                if (containers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No containers added. Add one from the sidebar.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    containers.forEachIndexed { index, container ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContainerVisualization(
                                container = container,
                                isSelected = selectedContainer == container.id,
                                isPouringFrom = pouringFromId == container.id,
                                isPouringTo = pouringToId == container.id,
                                onClick = { viewModel.selectContainer(container.id) },
                                modifier = Modifier.size(80.dp)
                            )
                            
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = getContainerProperties(container.type).displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${container.currentVolume.toInt()}/${container.capacity.toInt()} ml",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (container.liquids.isNotEmpty()) {
                                    Text(
                                        text = container.liquids.joinToString(", ") { it.liquidType.displayName },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.removeContainer(container.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reaction Temperature Display
                if (reactionActive != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6B6B)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "\uD83D\uDD25 Active Reaction!",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Temperature: ${temperature.toInt()}°C",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Reaction Log
                Text(
                    text = "Reaction Log",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (reactionLog.isEmpty()) {
                    Text(
                        text = "No reactions yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(reactionLog) { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // Control Panel (Right)
            ControlPanel(
                viewModel = viewModel,
                selectedContainer = selectedContainer,
                containers = containers,
                pouringFromId = pouringFromId,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun ControlPanel(
    viewModel: ChemistryLabViewModel,
    selectedContainer: String?,
    containers: List<Container>,
    pouringFromId: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Control Panel",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Divider()

        // Add Container Section
        Text(
            text = "Add Containers",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Container Types
        val containerTypes = listOf(
            "Test Tubes" to listOf(
                ContainerType.TEST_TUBE_SMALL,
                ContainerType.TEST_TUBE_MEDIUM,
                ContainerType.TEST_TUBE_LARGE
            ),
            "Beakers" to listOf(
                ContainerType.BEAKER_SMALL,
                ContainerType.BEAKER_MEDIUM,
                ContainerType.BEAKER_LARGE
            ),
            "Flasks" to listOf(
                ContainerType.FLASK_SMALL,
                ContainerType.FLASK_MEDIUM,
                ContainerType.FLASK_LARGE
            ),
            "Graduated Cylinders" to listOf(
                ContainerType.GRADUATED_CYLINDER_SMALL,
                ContainerType.GRADUATED_CYLINDER_MEDIUM,
                ContainerType.GRADUATED_CYLINDER_LARGE
            ),
            "Petri Dishes" to listOf(
                ContainerType.PETRI_DISH_SMALL,
                ContainerType.PETRI_DISH_MEDIUM,
                ContainerType.PETRI_DISH_LARGE
            )
        )

        containerTypes.forEach { (category, types) ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                types.forEach { type ->
                    Button(
                        onClick = { viewModel.addContainer(type) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = getContainerProperties(type).displayName,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        Divider()

        // Add Liquids Section
        if (selectedContainer != null) {
            Text(
                text = "Add Liquids",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            LiquidType.values().forEach { liquidType ->
                Button(
                    onClick = { viewModel.selectLiquid(liquidType) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = liquidType.color
                    )
                ) {
                    Text(
                        text = liquidType.displayName,
                        color = if (liquidType == LiquidType.MYSTERY) Color.White else Color.Black,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Divider()

            // Actions
            Text(
                text = "Actions",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = { 
                    if (pouringFromId == null) {
                        viewModel.startPouring(selectedContainer)
                    } else {
                        viewModel.finishPouring(selectedContainer)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pouringFromId != null) Color(0xFF00FF00) else Color(0xFF6750A4)
                )
            ) {
                Text(
                    text = if (pouringFromId != null) "Pour To" else "Pour From",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Button(
                onClick = { viewModel.mixLiquids(selectedContainer) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Mix",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Button(
                onClick = { viewModel.clearContainer(selectedContainer) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text(
                    text = "Clear",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "Select a container to add liquids",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.clearAllContainers() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513)
            )
        ) {
            Text(
                text = "Clear All",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}
