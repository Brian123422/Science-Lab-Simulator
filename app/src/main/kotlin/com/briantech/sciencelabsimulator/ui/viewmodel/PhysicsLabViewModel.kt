package com.briantech.sciencelabsimulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briantech.sciencelabsimulator.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PhysicsLabViewModel : ViewModel() {

    private val _currentSimulation = MutableStateFlow(PhysicsSimulation.GRAVITY)
    val currentSimulation: StateFlow<PhysicsSimulation> = _currentSimulation.asStateFlow()

    private val _gravityState = MutableStateFlow(GravityState())
    val gravityState: StateFlow<GravityState> = _gravityState.asStateFlow()

    private val _forceMotionState = MutableStateFlow(ForceMotionState())
    val forceMotionState: StateFlow<ForceMotionState> = _forceMotionState.asStateFlow()

    private val _pendulumState = MutableStateFlow(PendulumState())
    val pendulumState: StateFlow<PendulumState> = _pendulumState.asStateFlow()

    private val _projectileState = MutableStateFlow(ProjectileState())
    val projectileState: StateFlow<ProjectileState> = _projectileState.asStateFlow()

    private val _circuitState = MutableStateFlow(CircuitState())
    val circuitState: StateFlow<CircuitState> = _circuitState.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private val _resultLog = MutableStateFlow<List<PhysicsResult>>(emptyList())
    val resultLog: StateFlow<List<PhysicsResult>> = _resultLog.asStateFlow()

    // Animation frame loop
    private var lastUpdateTime = 0L

    fun selectSimulation(simulation: PhysicsSimulation) {
        _currentSimulation.value = simulation
        stopSimulation()
    }

    // ===================== GRAVITY SIMULATION =====================

    fun setGravityMass(mass: Float) {
        _gravityState.value = _gravityState.value.copy(mass = mass.coerceIn(0.1f, 100f))
    }

    fun setGravityValue(gravity: Float) {
        _gravityState.value = _gravityState.value.copy(gravity = gravity.coerceIn(0f, 20f))
    }

    fun setGravityHeight(height: Float) {
        _gravityState.value = _gravityState.value.copy(height = height.coerceIn(0f, 500f))
    }

    fun resetGravity() {
        _gravityState.value = GravityState()
        stopSimulation()
    }

    // ===================== FORCE & MOTION SIMULATION =====================

    fun setForceMotionMass(mass: Float) {
        _forceMotionState.value = _forceMotionState.value.copy(mass = mass.coerceIn(0.1f, 100f))
    }

    fun setAppliedForce(force: Float) {
        _forceMotionState.value = _forceMotionState.value.copy(appliedForce = force.coerceIn(0f, 100f))
    }

    fun setFriction(friction: Float) {
        _forceMotionState.value = _forceMotionState.value.copy(friction = friction.coerceIn(0f, 1f))
    }

    fun resetForceMotion() {
        _forceMotionState.value = ForceMotionState()
        stopSimulation()
    }

    // ===================== PENDULUM SIMULATION =====================

    fun setPendulumLength(length: Float) {
        _pendulumState.value = _pendulumState.value.copy(length = length.coerceIn(0.1f, 5f))
    }

    fun setPendulumMass(mass: Float) {
        _pendulumState.value = _pendulumState.value.copy(mass = mass.coerceIn(0.1f, 100f))
    }

    fun setPendulumAngle(angle: Float) {
        _pendulumState.value = _pendulumState.value.copy(angle = angle.coerceIn(-90f, 90f))
    }

    fun setPendulumGravity(gravity: Float) {
        _pendulumState.value = _pendulumState.value.copy(gravity = gravity.coerceIn(0f, 20f))
    }

    fun resetPendulum() {
        _pendulumState.value = PendulumState()
        stopSimulation()
    }

    // ===================== PROJECTILE MOTION SIMULATION =====================

    fun setProjectileVelocity(velocity: Float) {
        _projectileState.value = _projectileState.value.copy(initialVelocity = velocity.coerceIn(1f, 100f))
    }

    fun setProjectileAngle(angle: Float) {
        _projectileState.value = _projectileState.value.copy(launchAngle = angle.coerceIn(0f, 90f))
    }

    fun setProjectileMass(mass: Float) {
        _projectileState.value = _projectileState.value.copy(mass = mass.coerceIn(0.1f, 100f))
    }

    fun resetProjectile() {
        _projectileState.value = ProjectileState()
        stopSimulation()
    }

    fun launchProjectile() {
        _projectileState.value = _projectileState.value.launch()
        startSimulation()
    }

    // ===================== CIRCUIT SIMULATION =====================

    fun addCircuitComponent(component: CircuitComponent) {
        _circuitState.value = _circuitState.value.addComponent(component)
    }

    fun setCircuitVoltage(voltage: Float) {
        _circuitState.value = _circuitState.value.copy(totalVoltage = voltage.coerceIn(0f, 220f))
        updateCircuitCurrent()
    }

    fun toggleCircuitSwitch() {
        _circuitState.value = _circuitState.value.copy(
            isComplete = !_circuitState.value.isComplete,
            isPowered = !_circuitState.value.isPowered
        )
        updateCircuitCurrent()
    }

    private fun updateCircuitCurrent() {
        val current = _circuitState.value.calculateCurrent()
        _circuitState.value = _circuitState.value.copy(totalCurrent = current)
    }

    fun resetCircuit() {
        _circuitState.value = CircuitState()
    }

    // ===================== SIMULATION CONTROL =====================

    fun startSimulation() {
        _isSimulating.value = true
        lastUpdateTime = System.currentTimeMillis()
        simulationStep()
    }

    fun stopSimulation() {
        _isSimulating.value = false
    }

    private fun simulationStep() {
        if (!_isSimulating.value) return

        val currentTime = System.currentTimeMillis()
        val deltaTime = ((currentTime - lastUpdateTime) / 1000f).coerceAtMost(0.016f) // Max 60fps
        lastUpdateTime = currentTime

        when (_currentSimulation.value) {
            PhysicsSimulation.GRAVITY -> {
                _gravityState.value = _gravityState.value.update(deltaTime)
                if (_gravityState.value.height <= 0f) {
                    stopSimulation()
                }
            }
            PhysicsSimulation.FORCE_MOTION -> {
                _forceMotionState.value = _forceMotionState.value.update(deltaTime)
            }
            PhysicsSimulation.PENDULUM -> {
                _pendulumState.value = _pendulumState.value.update(deltaTime)
            }
            PhysicsSimulation.PROJECTILE -> {
                _projectileState.value = _projectileState.value.update(deltaTime)
                if (!_projectileState.value.hasLaunched) {
                    stopSimulation()
                    logResult()
                }
            }
            PhysicsSimulation.CIRCUIT -> {
                // Circuit doesn't need continuous updates
                stopSimulation()
            }
        }

        viewModelScope.launch {
            // Schedule next frame
            kotlinx.coroutines.delay(16) // ~60fps
            simulationStep()
        }
    }

    // ===================== RESULTS & LOGGING =====================

    private fun logResult() {
        val result = when (_currentSimulation.value) {
            PhysicsSimulation.GRAVITY -> {
                val state = _gravityState.value
                PhysicsResult(
                    simulationType = PhysicsSimulation.GRAVITY,
                    parameters = mapOf(
                        "mass" to state.mass,
                        "gravity" to state.gravity,
                        "initialHeight" to state.height
                    ),
                    results = mapOf(
                        "finalVelocity" to state.velocity,
                        "timeToGround" to state.time,
                        "force" to state.getForce(),
                        "potentialEnergy" to state.getPotentialEnergy(),
                        "kineticEnergy" to state.getKineticEnergy()
                    )
                )
            }
            PhysicsSimulation.FORCE_MOTION -> {
                val state = _forceMotionState.value
                PhysicsResult(
                    simulationType = PhysicsSimulation.FORCE_MOTION,
                    parameters = mapOf(
                        "mass" to state.mass,
                        "appliedForce" to state.appliedForce,
                        "friction" to state.friction
                    ),
                    results = mapOf(
                        "acceleration" to state.getAcceleration(),
                        "finalVelocity" to state.velocity,
                        "distance" to state.position,
                        "kinematicEnergy" to state.getKineticEnergy()
                    )
                )
            }
            PhysicsSimulation.PENDULUM -> {
                val state = _pendulumState.value
                PhysicsResult(
                    simulationType = PhysicsSimulation.PENDULUM,
                    parameters = mapOf(
                        "length" to state.length,
                        "mass" to state.mass,
                        "initialAngle" to state.angle,
                        "gravity" to state.gravity
                    ),
                    results = mapOf(
                        "period" to state.getPeriod(),
                        "maxVelocity" to state.getVelocity(),
                        "maxHeight" to state.getHeight()
                    )
                )
            }
            PhysicsSimulation.PROJECTILE -> {
                val state = _projectileState.value
                PhysicsResult(
                    simulationType = PhysicsSimulation.PROJECTILE,
                    parameters = mapOf(
                        "initialVelocity" to state.initialVelocity,
                        "launchAngle" to state.launchAngle,
                        "mass" to state.mass
                    ),
                    results = mapOf(
                        "range" to state.getRange(),
                        "maxHeight" to state.getMaxHeight(),
                        "finalX" to state.x,
                        "finalY" to state.y,
                        "timeOfFlight" to state.time
                    )
                )
            }
            PhysicsSimulation.CIRCUIT -> {
                val state = _circuitState.value
                PhysicsResult(
                    simulationType = PhysicsSimulation.CIRCUIT,
                    parameters = mapOf(
                        "voltage" to state.totalVoltage,
                        "resistance" to state.totalResistance
                    ),
                    results = mapOf(
                        "current" to state.totalCurrent,
                        "power" to state.calculatePower()
                    )
                )
            }
        }

        val currentLog = _resultLog.value.toMutableList()
        currentLog.add(0, result)
        _resultLog.value = currentLog.take(10) // Keep last 10 results
    }

    fun clearResults() {
        _resultLog.value = emptyList()
    }
}
