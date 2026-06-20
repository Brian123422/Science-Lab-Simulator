package com.briantech.sciencelabsimulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briantech.sciencelabsimulator.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChemistryLabViewModel : ViewModel() {

    private val _containers = MutableStateFlow<List<Container>>(emptyList())
    val containers: StateFlow<List<Container>> = _containers.asStateFlow()

    private val _selectedContainer = MutableStateFlow<String?>(null)
    val selectedContainer: StateFlow<String?> = _selectedContainer.asStateFlow()

    private val _selectedLiquid = MutableStateFlow<LiquidType?>(null)
    val selectedLiquid: StateFlow<LiquidType?> = _selectedLiquid.asStateFlow()

    private val _reactionActive = MutableStateFlow<ChemicalReaction?>(null)
    val reactionActive: StateFlow<ChemicalReaction?> = _reactionActive.asStateFlow()

    private val _reactionLog = MutableStateFlow<List<String>>(emptyList())
    val reactionLog: StateFlow<List<String>> = _reactionLog.asStateFlow()

    private val _temperature = MutableStateFlow(20f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _pouringFromId = MutableStateFlow<String?>(null)
    val pouringFromId: StateFlow<String?> = _pouringFromId.asStateFlow()

    private val _pouringToId = MutableStateFlow<String?>(null)
    val pouringToId: StateFlow<String?> = _pouringToId.asStateFlow()

    // Containers added for demonstration
    init {
        addContainer(ContainerType.BEAKER_MEDIUM)
        addContainer(ContainerType.TEST_TUBE_LARGE)
        addContainer(ContainerType.FLASK_MEDIUM)
    }

    fun addContainer(type: ContainerType) {
        val currentContainers = _containers.value.toMutableList()
        val props = getContainerProperties(type)
        
        val xPosition = (currentContainers.size % 3) * 120f + 50f
        val yPosition = (currentContainers.size / 3) * 200f + 100f
        
        val container = Container(
            id = UUID.randomUUID().toString(),
            type = type,
            position = Pair(xPosition, yPosition),
            capacity = props.capacity,
            currentVolume = 0f,
            liquids = emptyList()
        )
        
        currentContainers.add(container)
        _containers.value = currentContainers
    }

    fun selectContainer(containerId: String) {
        val current = _selectedContainer.value
        if (current == containerId) {
            _selectedContainer.value = null
        } else {
            _selectedContainer.value = containerId
        }
    }

    fun selectLiquid(liquidType: LiquidType) {
        val selectedContainerId = _selectedContainer.value
        if (selectedContainerId != null) {
            addLiquidToContainer(selectedContainerId, liquidType, 20f)
            _selectedLiquid.value = liquidType
        }
    }

    fun addLiquidToContainer(containerId: String, liquidType: LiquidType, volume: Float) {
        val currentContainers = _containers.value.toMutableList()
        val index = currentContainers.indexOfFirst { it.id == containerId }
        
        if (index != -1) {
            val container = currentContainers[index]
            val newVolume = (container.currentVolume + volume).coerceAtMost(container.capacity)
            val volumeToAdd = newVolume - container.currentVolume
            
            if (volumeToAdd > 0) {
                val newLiquid = ContainerLiquid(liquidType, volumeToAdd)
                val updatedLiquids = container.liquids.toMutableList().apply { add(newLiquid) }
                
                currentContainers[index] = container.copy(
                    currentVolume = newVolume,
                    liquids = updatedLiquids
                )
                
                _containers.value = currentContainers
                
                // Log the action
                addReactionLog("Added $volumeToAdd ml of ${liquidType.displayName} to container")
            }
        }
    }

    fun startPouring(fromId: String) {
        _pouringFromId.value = fromId
        _pouringToId.value = null
    }

    fun finishPouring(toId: String) {
        val fromId = _pouringFromId.value
        if (fromId != null && fromId != toId) {
            pourLiquid(fromId, toId)
        }
        _pouringFromId.value = null
        _pouringToId.value = null
    }

    fun cancelPouring() {
        _pouringFromId.value = null
        _pouringToId.value = null
    }

    private fun pourLiquid(fromId: String, toId: String) {
        val currentContainers = _containers.value.toMutableList()
        val fromIndex = currentContainers.indexOfFirst { it.id == fromId }
        val toIndex = currentContainers.indexOfFirst { it.id == toId }
        
        if (fromIndex != -1 && toIndex != -1) {
            val fromContainer = currentContainers[fromIndex]
            val toContainer = currentContainers[toIndex]
            
            // Calculate how much can be poured
            val spaceInTo = toContainer.capacity - toContainer.currentVolume
            val volumeToPour = minOf(fromContainer.currentVolume, spaceInTo)
            
            if (volumeToPour > 0) {
                // Pour all liquids
                val updatedLiquids = mutableListOf<ContainerLiquid>()
                var remainingVolume = volumeToPour
                
                for (liquid in fromContainer.liquids) {
                    if (remainingVolume <= 0) break
                    val volumeToTransfer = minOf(liquid.volume, remainingVolume)
                    if (volumeToTransfer > 0) {
                        updatedLiquids.add(liquid.copy(volume = volumeToTransfer))
                        remainingVolume -= volumeToTransfer
                    }
                }
                
                // Update from container
                currentContainers[fromIndex] = fromContainer.copy(
                    currentVolume = (fromContainer.currentVolume - volumeToPour).coerceAtLeast(0f),
                    liquids = fromContainer.liquids.mapNotNull { liquid ->
                        val newVolume = liquid.volume - minOf(liquid.volume, volumeToPour)
                        if (newVolume > 0) liquid.copy(volume = newVolume) else null
                    }
                )
                
                // Update to container
                currentContainers[toIndex] = toContainer.copy(
                    currentVolume = toContainer.currentVolume + volumeToPour,
                    liquids = (toContainer.liquids + updatedLiquids).coerceAtMost()
                )
                
                _containers.value = currentContainers
                
                addReactionLog("Poured $volumeToPour ml from container to container")
                
                // Check for reactions
                checkForReaction(toIndex)
            }
        }
    }

    fun mixLiquids(containerId: String) {
        val currentContainers = _containers.value.toMutableList()
        val index = currentContainers.indexOfFirst { it.id == containerId }
        
        if (index != -1) {
            val container = currentContainers[index]
            
            if (container.liquids.size >= 2) {
                checkForReaction(index)
                
                // Visual effect: shake the container
                addReactionLog("Mixing liquids in container...")
            }
        }
    }

    private fun checkForReaction(containerIndex: Int) {
        val containers = _containers.value
        if (containerIndex >= containers.size) return
        
        val container = containers[containerIndex]
        
        if (container.liquids.size >= 2) {
            val liquid1 = container.liquids[0].liquidType
            val liquid2 = container.liquids[1].liquidType
            
            val reaction = ReactionRules.getReaction(liquid1, liquid2)
            
            if (reaction != null) {
                _reactionActive.value = reaction
                
                // Update temperature
                _temperature.value = reaction.temperature
                
                // Mark container as reacting
                val updatedContainers = containers.toMutableList()
                updatedContainers[containerIndex] = container.copy(isReacting = true)
                _containers.value = updatedContainers
                
                // Add to log
                addReactionLog(reaction.description)
                
                // Reset reaction after delay
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    val latestContainers = _containers.value.toMutableList()
                    if (containerIndex < latestContainers.size) {
                        latestContainers[containerIndex] = latestContainers[containerIndex].copy(isReacting = false)
                        _containers.value = latestContainers
                    }
                    _reactionActive.value = null
                }
            }
        }
    }

    fun clearContainer(containerId: String) {
        val currentContainers = _containers.value.toMutableList()
        val index = currentContainers.indexOfFirst { it.id == containerId }
        
        if (index != -1) {
            currentContainers[index] = currentContainers[index].copy(
                currentVolume = 0f,
                liquids = emptyList(),
                temperature = 20f
            )
            _containers.value = currentContainers
            addReactionLog("Cleared container")
        }
    }

    fun removeContainer(containerId: String) {
        val currentContainers = _containers.value.toMutableList()
        currentContainers.removeAll { it.id == containerId }
        _containers.value = currentContainers
        
        if (_selectedContainer.value == containerId) {
            _selectedContainer.value = null
        }
        addReactionLog("Removed container")
    }

    private fun addReactionLog(message: String) {
        val currentLog = _reactionLog.value.toMutableList()
        currentLog.add(0, "[${System.currentTimeMillis() % 60000}ms] $message")
        _reactionLog.value = currentLog.take(10) // Keep last 10 logs
    }

    fun clearAllContainers() {
        _containers.value = emptyList()
        _selectedContainer.value = null
        _selectedLiquid.value = null
        _reactionActive.value = null
        addReactionLog("Cleared all containers")
    }
}
