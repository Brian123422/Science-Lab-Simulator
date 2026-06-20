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
import com.briantech.sciencelabsimulator.ui.viewmodel.PhysicsLabViewModel

@Composable
fun PhysicsDetailScreen(
    navController: NavController,
    viewModel: PhysicsLabViewModel = viewModel()
) {
    val currentSimulation by viewModel.currentSimulation.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val gravityState by viewModel.gravityState.collectAsState()
    val forceMotionState by viewModel.forceMotionState.collectAsState()
    val pendulumState by viewModel.pendulumState.collectAsState()
    val projectileState by viewModel.projectileState.collectAsState()
    val circuitState by viewModel.circuitState.collectAsState()
    val resultLog by viewModel.resultLog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Physics Lab",
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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Panel - Simulation Selection
            SimulationSelector(
                currentSimulation = currentSimulation,
                onSimulationSelected = { viewModel.selectSimulation(it) },
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            )

            // Center Panel - Visualization & Controls
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (currentSimulation) {
                    PhysicsSimulation.GRAVITY -> {
                        GravitySimulationPanel(
                            state = gravityState,
                            isSimulating = isSimulating,
                            onStartStop = { if (isSimulating) viewModel.stopSimulation() else viewModel.startSimulation() },
                            onReset = { viewModel.resetGravity() },
                            viewModel = viewModel
                        )
                    }
                    PhysicsSimulation.FORCE_MOTION -> {
                        ForceMotionSimulationPanel(
                            state = forceMotionState,
                            isSimulating = isSimulating,
                            onStartStop = { if (isSimulating) viewModel.stopSimulation() else viewModel.startSimulation() },
                            onReset = { viewModel.resetForceMotion() },
                            viewModel = viewModel
                        )
                    }
                    PhysicsSimulation.PENDULUM -> {
                        PendulumSimulationPanel(
                            state = pendulumState,
                            isSimulating = isSimulating,
                            onStartStop = { if (isSimulating) viewModel.stopSimulation() else viewModel.startSimulation() },
                            onReset = { viewModel.resetPendulum() },
                            viewModel = viewModel
                        )
                    }
                    PhysicsSimulation.PROJECTILE -> {
                        ProjectileSimulationPanel(
                            state = projectileState,
                            isSimulating = isSimulating,
                            onLaunch = { viewModel.launchProjectile() },
                            onReset = { viewModel.resetProjectile() },
                            viewModel = viewModel
                        )
                    }
                    PhysicsSimulation.CIRCUIT -> {
                        CircuitSimulationPanel(
                            state = circuitState,
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Right Panel - Results Log
            ResultsPanel(
                results = resultLog,
                onClear = { viewModel.clearResults() },
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun SimulationSelector(
    currentSimulation: PhysicsSimulation,
    onSimulationSelected: (PhysicsSimulation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Simulations",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        PhysicsSimulation.values().forEach { simulation ->
            Button(
                onClick = { onSimulationSelected(simulation) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentSimulation == simulation) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = simulation.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun GravitySimulationPanel(
    state: GravityState,
    isSimulating: Boolean,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    viewModel: PhysicsLabViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Gravity Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        GravityVisualization(state = state)

        // Controls
        ControlSlider(
            label = "Mass",
            value = state.mass,
            onValueChange = { viewModel.setGravityMass(it) },
            range = 0.1f..100f,
            unit = "kg"
        )

        ControlSlider(
            label = "Gravity",
            value = state.gravity,
            onValueChange = { viewModel.setGravityValue(it) },
            range = 0f..20f,
            unit = "m/s²"
        )

        ControlSlider(
            label = "Height",
            value = state.height,
            onValueChange = { viewModel.setGravityHeight(it) },
            range = 0f..500f,
            unit = "m"
        )

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onStartStop,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulating) Color(0xFF00FF00) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isSimulating) "Stop" else "Start")
            }
            Button(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun ForceMotionSimulationPanel(
    state: ForceMotionState,
    isSimulating: Boolean,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    viewModel: PhysicsLabViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Force & Motion Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        ForceMotionVisualization(state = state)

        // Controls
        ControlSlider(
            label = "Mass",
            value = state.mass,
            onValueChange = { viewModel.setForceMotionMass(it) },
            range = 0.1f..100f,
            unit = "kg"
        )

        ControlSlider(
            label = "Applied Force",
            value = state.appliedForce,
            onValueChange = { viewModel.setAppliedForce(it) },
            range = 0f..100f,
            unit = "N"
        )

        ControlSlider(
            label = "Friction",
            value = state.friction,
            onValueChange = { viewModel.setFriction(it) },
            range = 0f..1f,
            unit = ""
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onStartStop,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulating) Color(0xFF00FF00) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isSimulating) "Stop" else "Start")
            }
            Button(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun PendulumSimulationPanel(
    state: PendulumState,
    isSimulating: Boolean,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    viewModel: PhysicsLabViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pendulum Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        PendulumVisualization(state = state)

        ControlSlider(
            label = "Length",
            value = state.length,
            onValueChange = { viewModel.setPendulumLength(it) },
            range = 0.1f..5f,
            unit = "m"
        )

        ControlSlider(
            label = "Initial Angle",
            value = state.angle,
            onValueChange = { viewModel.setPendulumAngle(it) },
            range = -90f..90f,
            unit = "°"
        )

        ControlSlider(
            label = "Gravity",
            value = state.gravity,
            onValueChange = { viewModel.setPendulumGravity(it) },
            range = 0f..20f,
            unit = "m/s²"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onStartStop,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulating) Color(0xFF00FF00) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isSimulating) "Stop" else "Start")
            }
            Button(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun ProjectileSimulationPanel(
    state: ProjectileState,
    isSimulating: Boolean,
    onLaunch: () -> Unit,
    onReset: () -> Unit,
    viewModel: PhysicsLabViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Projectile Motion Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        ProjectileVisualization(state = state)

        ControlSlider(
            label = "Initial Velocity",
            value = state.initialVelocity,
            onValueChange = { viewModel.setProjectileVelocity(it) },
            range = 1f..100f,
            unit = "m/s"
        )

        ControlSlider(
            label = "Launch Angle",
            value = state.launchAngle,
            onValueChange = { viewModel.setProjectileAngle(it) },
            range = 0f..90f,
            unit = "°"
        )

        ControlSlider(
            label = "Mass",
            value = state.mass,
            onValueChange = { viewModel.setProjectileMass(it) },
            range = 0.1f..100f,
            unit = "kg"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onLaunch,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                enabled = !state.hasLaunched,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Launch")
            }
            Button(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun CircuitSimulationPanel(
    state: CircuitState,
    viewModel: PhysicsLabViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Circuit Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        CircuitVisualization(state = state)

        ControlSlider(
            label = "Voltage",
            value = state.totalVoltage,
            onValueChange = { viewModel.setCircuitVoltage(it) },
            range = 0f..220f,
            unit = "V"
        )

        Button(
            onClick = { viewModel.toggleCircuitSwitch() },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isPowered) Color(0xFF00FF00) else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(if (state.isPowered) "Circuit ON" else "Circuit OFF")
        }
    }
}

@Composable
fun ControlSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    unit: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
            Text(
                text = "${String.format("%.2f", value)} $unit",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ResultsPanel(
    results: List<PhysicsResult>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Results",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Clear",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Red
                )
            }
        }

        if (results.isEmpty()) {
            Text(
                text = "No results yet",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                results.forEach { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = result.simulationType.name,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            result.results.forEach { (key, value) ->
                                Text(
                                    text = "$key: ${String.format("%.2f", value)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 7.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
