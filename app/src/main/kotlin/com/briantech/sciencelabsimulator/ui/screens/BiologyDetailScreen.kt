package com.briantech.sciencelabsimulator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.briantech.sciencelabsimulator.R
import com.briantech.sciencelabsimulator.data.model.*
import com.briantech.sciencelabsimulator.ui.components.*
import com.briantech.sciencelabsimulator.ui.viewmodel.BiologyLabViewModel

@Composable
fun BiologyDetailScreen(
    navController: NavController,
    viewModel: BiologyLabViewModel = viewModel()
) {
    val cells by viewModel.cells.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val selectedOrganelle by viewModel.selectedOrganelle.collectAsState()
    val dnaSequences by viewModel.dnaSequences.collectAsState()
    val microscopeSettings by viewModel.microscopeSettings.collectAsState()
    val currentProcess by viewModel.currentProcess.collectAsState()
    val observations by viewModel.observations.collectAsState()
    val cellHealth by viewModel.cellHealth.collectAsState()
    val selectedCellType by viewModel.selectedCellType.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Biology Lab - Microscope",
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

        TabRow(selectedTabIndex = tabIndex) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = { Text("Cells", fontSize = 12.sp) }
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = { Text("DNA", fontSize = 12.sp) }
            )
            Tab(
                selected = tabIndex == 2,
                onClick = { tabIndex = 2 },
                text = { Text("Processes", fontSize = 12.sp) }
            )
            Tab(
                selected = tabIndex == 3,
                onClick = { tabIndex = 3 },
                text = { Text("Settings", fontSize = 12.sp) }
            )
        }

        when (tabIndex) {
            0 -> CellsTab(
                cells = cells,
                selectedCell = selectedCell,
                selectedOrganelle = selectedOrganelle,
                cellHealth = cellHealth,
                microscopeSettings = microscopeSettings,
                onCellSelect = { viewModel.selectCell(it) },
                onOrganelleSelect = { viewModel.selectOrganelle(it) },
                onAddCell = { viewModel.selectCellType(selectedCellType); viewModel.addCell() },
                onRemoveCell = { viewModel.removeCell(it) },
                onClearAll = { viewModel.clearAll() },
                viewModel = viewModel
            )
            1 -> DNATab(
                sequences = dnaSequences,
                microscopeSettings = microscopeSettings,
                viewModel = viewModel
            )
            2 -> ProcessesTab(
                currentProcess = currentProcess,
                observations = observations,
                onStartProcess = { viewModel.startProcess(it) },
                onStopProcess = { viewModel.stopProcess() },
                onResetProcess = { viewModel.resetProcess() }
            )
            3 -> SettingsTab(
                settings = microscopeSettings,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun CellsTab(
    cells: List<CellState>,
    selectedCell: String?,
    selectedOrganelle: String?,
    cellHealth: Map<String, CellHealthState>,
    microscopeSettings: MicroscopeSettings,
    onCellSelect: (String?) -> Unit,
    onOrganelleSelect: (String?) -> Unit,
    onAddCell: () -> Unit,
    onRemoveCell: (String) -> Unit,
    onClearAll: () -> Unit,
    viewModel: BiologyLabViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main microscope view
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = when (microscopeSettings.colorFilter) {
                        ColorFilter.FLUORESCENCE -> Color.Black
                        ColorFilter.HEAT_MAP -> Color(0xFF1a1a1a)
                        ColorFilter.GRAYSCALE -> Color.DarkGray
                        ColorFilter.PHASE_CONTRAST -> Color(0xFF2a2a2a)
                        ColorFilter.NORMAL -> Color(0xFFFAFAFA)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (cells.isEmpty()) {
                Text(
                    text = "No cells observed. Add a cell to begin.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            } else {
                // Cells grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cells.chunked(2).forEach { rowCells ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowCells.forEach { cell ->
                                CellVisualization(
                                    cell = cell,
                                    isSelected = selectedCell == cell.id,
                                    onCellClick = { onCellSelect(cell.id) },
                                    onOrganelleClick = { onOrganelleSelect(it) },
                                    settings = microscopeSettings,
                                    modifier = Modifier.size(160.dp)
                                )
                            }
                            if (rowCells.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Right panel - Controls & Details
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cell Controls",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Add cell buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CellType.values().forEach { cellType ->
                    Button(
                        onClick = {
                            viewModel.selectCellType(cellType)
                            onAddCell()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Add ${cellType.name.replace("_", " ")}",
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Divider()

            // Selected organelle details
            if (selectedOrganelle != null) {
                val selectedCell = cells.find { it.id == selectedCell }
                val organelleInstance = selectedCell?.organelles?.find { it.id == selectedOrganelle }
                
                if (organelleInstance != null) {
                    Text(
                        text = "Organelle Details",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OrganelleInfoCard(organelleInstance.organelle)
                }
            }

            // Cell health if selected
            if (selectedCell != null) {
                val health = cellHealth[selectedCell]
                if (health != null) {
                    CellHealthIndicator(health)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom buttons
            Button(
                onClick = onClearAll,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Clear All", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun DNATab(
    sequences: List<DNASequence>,
    microscopeSettings: MicroscopeSettings,
    viewModel: BiologyLabViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "DNA Sequence Viewer",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (sequences.isEmpty()) {
            Text(
                text = "No DNA sequences loaded",
                color = Color.Gray
            )
        } else {
            sequences.forEach { sequence ->
                DNAVisualization(
                    sequence = sequence,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Function: ${sequence.geneFunction}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProcessesTab(
    currentProcess: CellProcess,
    observations: List<MicroscopeObservation>,
    onStartProcess: (BiologicalProcess) -> Unit,
    onStopProcess: () -> Unit,
    onResetProcess: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Process selector
        Column(
            modifier = Modifier
                .weight(0.4f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Biological Processes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            listOf(
                BiologicalProcess.MITOSIS,
                BiologicalProcess.MEIOSIS,
                BiologicalProcess.PHOTOSYNTHESIS,
                BiologicalProcess.RESPIRATION,
                BiologicalProcess.PROTEIN_SYNTHESIS,
                BiologicalProcess.DNA_REPLICATION
            ).forEach { process ->
                Button(
                    onClick = { onStartProcess(process) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentProcess.processType != process || !currentProcess.isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = process.name.replace("_", " "),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Process status
        Column(
            modifier = Modifier
                .weight(0.6f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentProcess.processType != BiologicalProcess.NONE) {
                ProcessIndicator(currentProcess)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStopProcess,
                        modifier = Modifier.weight(1f),
                        enabled = currentProcess.isRunning
                    ) {
                        Text("Stop")
                    }
                    Button(
                        onClick = onResetProcess,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                }
            } else {
                Text(
                    text = "Select a process to begin",
                    color = Color.Gray
                )
            }

            Divider()

            Text(
                text = "Recent Observations",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )

            observations.forEach { obs ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = obs.cellType.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${obs.organellesObserved.size} organelles observed",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTab(
    settings: MicroscopeSettings,
    viewModel: BiologyLabViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Microscope Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Magnification
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Magnification", fontSize = 10.sp)
                Text("${settings.magnification.toInt()}x", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = settings.magnification,
                onValueChange = { viewModel.setMagnification(it) },
                valueRange = 40f..1000f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Brightness
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Brightness", fontSize = 10.sp)
                Text("${(settings.brightness * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = settings.brightness,
                onValueChange = { viewModel.setBrightness(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Contrast
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contrast", fontSize = 10.sp)
                Text("${(settings.contrast * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = settings.contrast,
                onValueChange = { viewModel.setContrast(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Focus Depth
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Focus Depth", fontSize = 10.sp)
                Text("${(settings.focusDepth * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = settings.focusDepth,
                onValueChange = { viewModel.setFocusDepth(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Divider()

        // Color Filters
        Text(
            text = "Color Filter",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ColorFilter.values().forEach { filter ->
                Button(
                    onClick = { viewModel.setColorFilter(filter) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.colorFilter == filter) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    Text(filter.name.replace("_", " "), fontSize = 9.sp)
                }
            }
        }

        Divider()

        // Grid toggle
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text("Show Grid", fontSize = 10.sp)
            Checkbox(
                checked = settings.showGrid,
                onCheckedChange = { viewModel.toggleGrid() }
            )
        }
    }
}
