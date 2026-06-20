package com.briantech.sciencelabsimulator.data.model

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Physics Simulation Types
enum class PhysicsSimulation {
    GRAVITY,
    FORCE_MOTION,
    PENDULUM,
    PROJECTILE,
    CIRCUIT
}

// Gravity Simulation
data class GravityState(
    val mass: Float = 1.0f,        // kg
    val gravity: Float = 9.8f,     // m/s²
    val height: Float = 100.0f,    // meters
    val velocity: Float = 0.0f,    // m/s (downward)
    val time: Float = 0.0f         // seconds
) {
    fun update(deltaTime: Float): GravityState {
        val newVelocity = velocity + (gravity * deltaTime)
        val newHeight = (height - (velocity * deltaTime + 0.5f * gravity * deltaTime * deltaTime)).coerceAtLeast(0f)
        val newTime = time + deltaTime
        
        return copy(
            velocity = newVelocity,
            height = newHeight,
            time = newTime
        )
    }
    
    fun getForce(): Float = mass * gravity
    fun getKineticEnergy(): Float = 0.5f * mass * velocity * velocity
    fun getPotentialEnergy(): Float = mass * gravity * height
}

// Force and Motion Simulation
data class ForceMotionState(
    val mass: Float = 1.0f,              // kg
    val appliedForce: Float = 10.0f,     // Newtons
    val friction: Float = 0.2f,          // coefficient (0-1)
    val velocity: Float = 0.0f,          // m/s
    val position: Float = 0.0f,          // meters
    val time: Float = 0.0f,              // seconds
    val isMoving: Boolean = false
) {
    fun update(deltaTime: Float): ForceMotionState {
        if (!isMoving) return this
        
        val normalForce = mass * 9.8f
        val frictionForce = friction * normalForce
        val netForce = appliedForce - frictionForce
        val acceleration = netForce / mass
        
        val newVelocity = (velocity + acceleration * deltaTime).coerceAtLeast(0f)
        val newPosition = position + (velocity * deltaTime + 0.5f * acceleration * deltaTime * deltaTime)
        val newTime = time + deltaTime
        
        return copy(
            velocity = newVelocity,
            position = newPosition,
            time = newTime,
            isMoving = newVelocity > 0.01f
        )
    }
    
    fun getAcceleration(): Float = (appliedForce - (friction * mass * 9.8f)) / mass
    fun getKineticEnergy(): Float = 0.5f * mass * velocity * velocity
}

// Pendulum Simulation
data class PendulumState(
    val length: Float = 1.0f,            // meters
    val mass: Float = 1.0f,              // kg
    val angle: Float = 45.0f,            // degrees
    val angularVelocity: Float = 0.0f,   // rad/s
    val time: Float = 0.0f,              // seconds
    val gravity: Float = 9.8f            // m/s²
) {
    fun update(deltaTime: Float): PendulumState {
        val angleRad = Math.toRadians(angle.toDouble()).toFloat()
        val angularAccel = -(gravity / length) * sin(angleRad)
        
        val newAngularVel = angularVelocity + angularAccel * deltaTime
        val newAngle = (angle + Math.toDegrees(angularVelocity.toDouble()).toFloat() * deltaTime)
            .let { a ->
                when {
                    a > 90f -> 90f
                    a < -90f -> -90f
                    else -> a
                }
            }
        
        return copy(
            angle = newAngle,
            angularVelocity = newAngularVel,
            time = time + deltaTime
        )
    }
    
    fun getHeight(): Float = length * (1f - cos(Math.toRadians(angle.toDouble())).toFloat())
    fun getVelocity(): Float = length * Math.abs(Math.toRadians(angularVelocity.toDouble())).toFloat()
    fun getPeriod(): Float = 2f * PI.toFloat() * sqrt(length / gravity)
}

// Projectile Motion Simulation
data class ProjectileState(
    val initialVelocity: Float = 20.0f,  // m/s
    val launchAngle: Float = 45.0f,      // degrees
    val mass: Float = 1.0f,              // kg
    val x: Float = 0.0f,                 // meters
    val y: Float = 0.0f,                 // meters
    val vx: Float = 0.0f,                // m/s (horizontal)
    val vy: Float = 0.0f,                // m/s (vertical)
    val time: Float = 0.0f,              // seconds
    val hasLaunched: Boolean = false,
    val gravity: Float = 9.8f            // m/s²
) {
    fun launch(): ProjectileState {
        val angleRad = Math.toRadians(launchAngle.toDouble()).toFloat()
        return copy(
            vx = initialVelocity * cos(angleRad),
            vy = initialVelocity * sin(angleRad),
            hasLaunched = true,
            x = 0f,
            y = 0f,
            time = 0f
        )
    }
    
    fun update(deltaTime: Float): ProjectileState {
        if (!hasLaunched) return this
        
        val newVy = vy - (gravity * deltaTime)
        val newX = x + (vx * deltaTime)
        val newY = (y + (vy * deltaTime - 0.5f * gravity * deltaTime * deltaTime)).coerceAtLeast(0f)
        val newTime = time + deltaTime
        
        val isFinished = newY <= 0f && time > 0f
        
        return copy(
            x = newX,
            y = newY,
            vx = vx,
            vy = newVy,
            time = newTime,
            hasLaunched = !isFinished
        )
    }
    
    fun getRange(): Float = (initialVelocity * initialVelocity * sin(2 * Math.toRadians(launchAngle.toDouble())).toFloat()) / 9.8f
    fun getMaxHeight(): Float = (initialVelocity * initialVelocity * sin(Math.toRadians(launchAngle.toDouble())).toFloat().pow(2)) / (2 * 9.8f)
}

fun Float.pow(exponent: Float): Float = kotlin.math.pow(this, exponent)

// Circuit Simulation
data class CircuitComponent(
    val id: String,
    val type: CircuitComponentType,
    val resistance: Float = 0f,          // Ohms
    val voltage: Float = 0f,             // Volts
    val current: Float = 0f,             // Amperes
    val position: Pair<Float, Float> = Pair(0f, 0f)
)

enum class CircuitComponentType {
    BATTERY,
    RESISTOR,
    LED,
    SWITCH,
    CAPACITOR,
    INDUCTOR
}

data class CircuitState(
    val components: List<CircuitComponent> = emptyList(),
    val totalVoltage: Float = 12.0f,     // Volts
    val totalResistance: Float = 0f,     // Ohms
    val totalCurrent: Float = 0f,        // Amperes
    val isComplete: Boolean = false,     // Circuit is complete
    val isPowered: Boolean = false
) {
    fun calculateCurrent(): Float {
        return if (isComplete && totalResistance > 0) {
            totalVoltage / totalResistance
        } else {
            0f
        }
    }
    
    fun calculatePower(): Float = totalVoltage * totalCurrent
    
    fun addComponent(component: CircuitComponent): CircuitState {
        val newComponents = components + component
        val newTotalResistance = newComponents
            .filter { it.type == CircuitComponentType.RESISTOR }
            .sumOf { it.resistance.toDouble() }.toFloat()
        
        return copy(
            components = newComponents,
            totalResistance = newTotalResistance
        )
    }
}

// Physics Experiment State
data class PhysicsExperiment(
    val id: String,
    val name: String,
    val simulationType: PhysicsSimulation,
    val description: String,
    val imageUrl: String = ""
)

// Results and Logs
data class PhysicsResult(
    val timestamp: Long = System.currentTimeMillis(),
    val simulationType: PhysicsSimulation,
    val parameters: Map<String, Float>,
    val results: Map<String, Float>,
    val notes: String = ""
)
