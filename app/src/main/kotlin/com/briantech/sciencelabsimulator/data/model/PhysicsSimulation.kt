package com.briantech.sciencelabsimulator.data.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Data classes for physics simulations
data class GravityObject(
    val id: String,
    val mass: Float,
    val x: Float,
    val y: Float,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val radius: Float = 5f
)

data class GravityState(
    val objects: List<GravityObject> = emptyList(),
    val time: Float = 0f,
    val gravityConstant: Float = 9.8f
)

data class ProjectileState(
    val x: Float = 0f,
    val y: Float = 0f,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val time: Float = 0f,
    val angle: Float = 45f,
    val initialVelocity: Float = 20f,
    val gravity: Float = 9.8f,
    val friction: Float = 0f,
    val isLaunched: Boolean = false,
    val trajectory: List<Pair<Float, Float>> = emptyList()
)

data class PendulumState(
    val angle: Float = 30f,
    val angularVelocity: Float = 0f,
    val angularAcceleration: Float = 0f,
    val length: Float = 100f,
    val mass: Float = 1f,
    val gravity: Float = 9.8f,
    val friction: Float = 0.02f,
    val time: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f
)

// Simulation engines
class GravitySimulator(initialState: GravityState = GravityState()) {
    var state: GravityState = initialState
        private set

    fun addObject(obj: GravityObject) {
        state = state.copy(objects = state.objects + obj)
    }

    fun removeObject(id: String) {
        state = state.copy(objects = state.objects.filter { it.id != id })
    }

    fun update(deltaTime: Float) {
        val newObjects = mutableListOf<GravityObject>()
        
        for (obj in state.objects) {
            var ax = 0f
            var ay = 0f

            // Calculate gravitational forces from other objects
            for (other in state.objects) {
                if (obj.id != other.id) {
                    val dx = other.x - obj.x
                    val dy = other.y - obj.y
                    val distSquared = dx * dx + dy * dy + 1f // Add small value to prevent division by zero
                    val dist = sqrt(distSquared)
                    
                    // F = G * m1 * m2 / r^2
                    val force = state.gravityConstant * obj.mass * other.mass / distSquared
                    
                    // Acceleration components
                    ax += (force / obj.mass) * (dx / dist)
                    ay += (force / obj.mass) * (dy / dist)
                }
            }

            // Update velocity and position
            val newVx = obj.vx + ax * deltaTime
            val newVy = obj.vy + ay * deltaTime
            val newX = obj.x + newVx * deltaTime
            val newY = obj.y + newVy * deltaTime

            newObjects.add(obj.copy(x = newX, y = newY, vx = newVx, vy = newVy))
        }

        state = state.copy(
            objects = newObjects,
            time = state.time + deltaTime
        )
    }

    fun reset() {
        state = GravityState()
    }
}

class ProjectileSimulator {
    var state: ProjectileState = ProjectileState()
        private set

    fun launch(angle: Float, velocity: Float, friction: Float = 0f) {
        val angleRad = Math.toRadians(angle.toDouble()).toFloat()
        state = ProjectileState(
            x = 0f,
            y = 0f,
            vx = velocity * cos(angleRad),
            vy = velocity * sin(angleRad),
            angle = angle,
            initialVelocity = velocity,
            friction = friction,
            isLaunched = true,
            trajectory = listOf(0f to 0f)
        )
    }

    fun update(deltaTime: Float) {
        if (!state.isLaunched) return

        var vx = state.vx
        var vy = state.vy

        // Apply friction (air resistance)
        if (state.friction > 0f) {
            val speed = sqrt(vx * vx + vy * vy)
            if (speed > 0f) {
                val frictionForce = state.friction * speed
                vx -= (frictionForce * vx / speed) * deltaTime
                vy -= (frictionForce * vy / speed) * deltaTime
            }
        }

        // Apply gravity
        vy -= state.gravity * deltaTime

        // Update position
        var newX = state.x + vx * deltaTime
        var newY = state.y + vy * deltaTime

        // Stop simulation if projectile hits ground
        var isLaunched = state.isLaunched
        if (newY < 0f) {
            newY = 0f
            isLaunched = false
        }

        val newTrajectory = state.trajectory + (newX to newY)

        state = state.copy(
            x = newX,
            y = newY,
            vx = vx,
            vy = vy,
            time = state.time + deltaTime,
            isLaunched = isLaunched,
            trajectory = newTrajectory
        )
    }

    fun reset() {
        state = ProjectileState()
    }

    fun setGravity(gravity: Float) {
        state = state.copy(gravity = gravity)
    }

    fun setFriction(friction: Float) {
        state = state.copy(friction = friction)
    }
}

class PendulumSimulator {
    var state: PendulumState = PendulumState()
        private set

    fun update(deltaTime: Float) {
        // Simple pendulum physics: θ'' + (g/L) * sin(θ) + friction * θ' = 0
        val g = state.gravity
        val l = state.length
        val friction = state.friction

        // Angular acceleration: a = -(g/L) * sin(θ) - friction * ω
        val newAngularAcceleration = -(g / l) * sin(Math.toRadians(state.angle.toDouble())).toFloat() - 
                                    friction * state.angularVelocity

        // Update angular velocity and angle
        val newAngularVelocity = state.angularVelocity + newAngularAcceleration * deltaTime
        val newAngle = state.angle + newAngularVelocity * deltaTime

        // Calculate position (assuming pivot at origin)
        val angleRad = Math.toRadians(newAngle.toDouble()).toFloat()
        val newX = l * sin(angleRad)
        val newY = l * cos(angleRad)

        state = state.copy(
            angle = newAngle,
            angularVelocity = newAngularVelocity,
            angularAcceleration = newAngularAcceleration,
            x = newX,
            y = newY,
            time = state.time + deltaTime
        )
    }

    fun reset() {
        state = PendulumState()
    }

    fun setAngle(angle: Float) {
        state = state.copy(angle = angle)
    }

    fun setLength(length: Float) {
        state = state.copy(length = length)
    }

    fun setGravity(gravity: Float) {
        state = state.copy(gravity = gravity)
    }

    fun setFriction(friction: Float) {
        state = state.copy(friction = friction)
    }
}
