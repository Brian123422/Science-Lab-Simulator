package com.briantech.sciencelabsimulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briantech.sciencelabsimulator.data.model.*

@Composable
fun CellVisualization(
    cell: CellState,
    isSelected: Boolean = false,
    onCellClick: () -> Unit,
    onOrganelleClick: (String) -> Unit,
    settings: MicroscopeSettings = MicroscopeSettings(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size((cell.radius * 2).dp)
            .background(
                color = when (settings.colorFilter) {
                    ColorFilter.GRAYSCALE -> Color.LightGray
                    ColorFilter.HEAT_MAP -> Color(0xFFFF0000).copy(alpha = 0.3f)
                    ColorFilter.FLUORESCENCE -> Color.Black
                    ColorFilter.PHASE_CONTRAST -> Color(0xFF808080)
                    ColorFilter.NORMAL -> Color.White
                }.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .border(
                width = if (isSelected) 3.dp else 2.dp,
                color = if (isSelected) Color(0xFF6750A4) else Color.Black,
                shape = CircleShape
            )
            .clickable { onCellClick() }
            .shadow(elevation = if (isSelected) 8.dp else 2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Cell wall for plant cells
        if (cell.cellType == CellType.PLANT_CELL) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = Color(0xFFD2691E),
                        shape = CircleShape
                    )
                    .padding(4.dp)
            )
        }

        // Cytoplasm
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(
                    color = Color(0xFFFFF8DC).copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )

        // Organelles
        cell.organelles.forEach { organelleInstance ->
            Box(
                modifier = Modifier
                    .size((organelleInstance.radius * 2).dp)
                    .background(
                        color = organelleInstance.organelle.color
                            .copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .clickable { onOrganelleClick(organelleInstance.id) }
                    .offset(
                        x = (organelleInstance.x - cell.centerX).dp,
                        y = (organelleInstance.y - cell.centerY).dp
                    )
                    .shadow(elevation = 2.dp)
                    .alpha(if (settings.brightness > 0) 1f else 0.5f)
            ) {
                Text(
                    text = organelleInstance.organelle.displayName.first().toString(),
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Cell type label
        Text(
            text = cell.cellType.name.replace("_", " "),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            color = Color.Black
        )
    }
}

@Composable
fun DNAVisualization(
    sequence: DNASequence,
    selectedBase: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = sequence.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = sequence.description,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 8.sp,
            color = Color.Gray
        )

        // DNA bases visualization
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            sequence.bases.forEach { base ->
                Column(
                    modifier = Modifier
                        .width(16.dp)
                        .background(
                            color = if (selectedBase == base.position) {
                                Color(0xFF6750A4)
                            } else {
                                base.type.color.copy(alpha = 0.7f)
                            },
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = base.type.letter,
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (base.pairedWith != null) {
                        Text(
                            text = base.pairedWith.letter,
                            fontSize = 5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrganelleInfoCard(
    organelle: Organelle,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(organelle.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .border(2.dp, organelle.color, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(organelle.color, CircleShape)
                )
                Text(
                    text = organelle.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = organelle.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun CellHealthIndicator(
    health: CellHealthState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Cell Health Status",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        HealthBar("Health", health.health, Color(0xFF00AA00))
        HealthBar("Energy", health.energy, Color(0xFFFFD700))
        HealthBar("Stress", health.stressLevel, Color(0xFFFF6B6B))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HealthStat("Temperature", "${String.format("%.1f", health.temperature)}°C")
            HealthStat("pH Level", String.format("%.1f", health.ph))
            HealthStat("Metabolism", "${String.format("%.2f", health.metabolism)}x")
        }
    }
}

@Composable
fun HealthBar(
    label: String,
    value: Float,
    color: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp
            )
            Text(
                text = "${value.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun HealthStat(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 9.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProcessIndicator(
    process: CellProcess,
    modifier: Modifier = Modifier
) {
    if (process.processType == BiologicalProcess.NONE) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Active Process: ${process.processType.name.replace("_", " ")}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6750A4)
        )

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(process.progress.coerceIn(0f, 1f))
                    .background(Color(0xFF00AA00), RoundedCornerShape(6.dp))
            )
        }

        Text(
            text = "${(process.progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 9.sp
        )

        // Current step
        if (process.steps.isNotEmpty()) {
            val currentStepIndex = (process.progress * process.steps.size).toInt()
                .coerceAtMost(process.steps.size - 1)
            
            Text(
                text = "Step: ${process.steps[currentStepIndex]}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 8.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun rememberScrollState(): androidx.compose.foundation.ScrollState {
    return androidx.compose.foundation.rememberScrollState()
}

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
