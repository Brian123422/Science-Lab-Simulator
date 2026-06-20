package com.briantech.sciencelabsimulator.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briantech.sciencelabsimulator.data.model.*
import kotlin.math.cos
import kotlin.math.sin

// Gravity Visualization
@Composable
fun GravityVisualization(
    state: GravityState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Ground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.dp)
        )

        // Height scale
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(5) {
                Text(
                    text = "${(5 - it) * 100}m",
                    fontSize = 8.sp,
                    color = Color.Gray
                )
            }
        }

        // Falling object
        val positionPercent = if (state.height > 0) 1f - (state.height / 100f) else 1f
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF6750A4), CircleShape)
                .align(Alignment.BottomStart)
                .offset(y = -(positionPercent * 350).dp)
                .padding(start = 20.dp)
                .shadow(elevation = 4.dp)
        )

        // Info panel
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InfoText("Height: ${state.height.toInt()}m")
            InfoText("Velocity: ${state.velocity.toInt()}m/s")
            InfoText("Time: ${String.format("%.2f", state.time)}s")
            InfoText("Force: ${String.format("%.2f", state.getForce())}N")
        }
    }
}

// Force & Motion Visualization
@Composable
fun ForceMotionVisualization(
    state: ForceMotionState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Ground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )

        // Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFFBBBBBB))
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.dp)
        )

        // Moving object
        val maxPosition = 200f
        val objectPosition = (state.position / 100f * maxPosition).coerceAtMost(maxPosition.dp)
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(Color(0xFF6750A4), RoundedCornerShape(4.dp))
                .align(Alignment.BottomStart)
                .offset(x = objectPosition)
                .padding(start = 10.dp, bottom = 20.dp)
                .shadow(elevation = 4.dp)
        )

        // Force arrow
        if (state.isMoving) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Red)
                    .align(Alignment.CenterEnd)
                    .padding(end = 100.dp)
            )
        }

        // Info panel
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InfoText("Distance: ${String.format("%.2f", state.position)}m")
            InfoText("Velocity: ${String.format("%.2f", state.velocity)}m/s")
            InfoText("Accel: ${String.format("%.2f", state.getAcceleration())}m/s²")
            InfoText("KE: ${String.format("%.2f", state.getKineticEnergy())}J")
        }
    }
}

// Pendulum Visualization
@Composable
fun PendulumVisualization(
    state: PendulumState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        val angleRad = Math.toRadians(state.angle.toDouble()).toFloat()
        val pivotX = 150.dp
        val pivotY = 50.dp
        val stringLength = 120.dp

        // Pivot point
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color.Black, CircleShape)
                .align(Alignment.TopCenter)
                .offset(x = -pivotX + 150.dp, y = pivotY)
        )

        // String
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(stringLength)
                .background(Color.Black)
                .align(Alignment.TopCenter)
                .offset(
                    x = -pivotX + 150.dp + (stringLength.value * sin(angleRad) / 2).dp,
                    y = pivotY + (stringLength.value * (1 - cos(angleRad)) / 2).dp
                )
                .rotate(state.angle)
        )

        // Bob
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF6750A4), CircleShape)
                .align(Alignment.TopCenter)
                .offset(
                    x = -pivotX + 150.dp + (stringLength.value * sin(angleRad)).dp,
                    y = pivotY + (stringLength.value * (1 - cos(angleRad))).dp
                )
                .shadow(elevation = 4.dp)
        )

        // Info panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InfoText("Angle: ${String.format("%.1f", state.angle)}°")
            InfoText("Period: ${String.format("%.2f", state.getPeriod())}s")
            InfoText("Max Height: ${String.format("%.2f", state.getHeight())}m")
            InfoText("Velocity: ${String.format("%.2f", state.getVelocity())}m/s")
        }
    }
}

// Projectile Motion Visualization
@Composable
fun ProjectileVisualization(
    state: ProjectileState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Ground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
        )

        // Projectile path
        if (state.hasLaunched || state.time > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color(0xFF6750A4), CircleShape)
                    .align(Alignment.BottomStart)
                    .offset(
                        x = ((state.x / state.getRange()) * 250).dp,
                        y = -((state.y / (state.getMaxHeight() + 10)) * 250).dp
                    )
                    .shadow(elevation = 4.dp)
            )
        }

        // Launch marker
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Green, CircleShape)
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 0.dp)
        )

        // Landing marker
        if (state.time > 0 && !state.hasLaunched) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.Red, CircleShape)
                    .align(Alignment.BottomStart)
                    .offset(x = ((state.getRange() / (state.getRange() + 10)) * 250).dp)
            )
        }

        // Info panel
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InfoText("Position X: ${String.format("%.2f", state.x)}m")
            InfoText("Position Y: ${String.format("%.2f", state.y)}m")
            InfoText("Velocity X: ${String.format("%.2f", state.vx)}m/s")
            InfoText("Velocity Y: ${String.format("%.2f", state.vy)}m/s")
            InfoText("Range: ${String.format("%.2f", state.getRange())}m")
            InfoText("Max Height: ${String.format("%.2f", state.getMaxHeight())}m")
        }
    }
}

// Circuit Visualization
@Composable
fun CircuitVisualization(
    state: CircuitState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Circuit visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
            ) {
                // Simple circuit diagram
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(40.dp)
                            .background(Color.Black)
                    )

                    // Resistor indicator
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(20.dp)
                            .background(
                                if (state.isPowered) Color(0xFF6750A4) else Color.Gray,
                                RoundedCornerShape(4.dp)
                            )
                    )

                    // LED indicator
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (state.isPowered) Color(0xFFFF6B6B) else Color.LightGray,
                                CircleShape
                            )
                    )
                }
            }

            // Stats
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoText("Voltage: ${String.format("%.2f", state.totalVoltage)}V")
                InfoText("Resistance: ${String.format("%.2f", state.totalResistance)}Ω")
                InfoText("Current: ${String.format("%.4f", state.totalCurrent)}A")
                InfoText("Power: ${String.format("%.2f", state.calculatePower())}W")
                InfoText("Status: ${if (state.isPowered) "ON" else "OFF"}")
            }
        }
    }
}

@Composable
fun InfoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 10.sp,
        color = Color.Black
    )
}
