package com.briantech.sciencelabsimulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.briantech.sciencelabsimulator.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

class BiologyLabViewModel : ViewModel() {

    private val _selectedCellType = MutableStateFlow(CellType.PLANT_CELL)
    val selectedCellType: StateFlow<CellType> = _selectedCellType.asStateFlow()

    private val _cells = MutableStateFlow<List<CellState>>(emptyList())
    val cells: StateFlow<List<CellState>> = _cells.asStateFlow()

    private val _selectedCell = MutableStateFlow<String?>(null)
    val selectedCell: StateFlow<String?> = _selectedCell.asStateFlow()

    private val _selectedOrganelle = MutableStateFlow<String?>(null)
    val selectedOrganelle: StateFlow<String?> = _selectedOrganelle.asStateFlow()

    private val _dnaSequences = MutableStateFlow<List<DNASequence>>(emptyList())
    val dnaSequences: StateFlow<List<DNASequence>> = _dnaSequences.asStateFlow()

    private val _microscopeSettings = MutableStateFlow(MicroscopeSettings())
    val microscopeSettings: StateFlow<MicroscopeSettings> = _microscopeSettings.asStateFlow()

    private val _currentProcess = MutableStateFlow(CellProcess(BiologicalProcess.NONE))
    val currentProcess: StateFlow<CellProcess> = _currentProcess.asStateFlow()

    private val _observations = MutableStateFlow<List<MicroscopeObservation>>(emptyList())
    val observations: StateFlow<List<MicroscopeObservation>> = _observations.asStateFlow()

    private val _cellHealth = MutableStateFlow<Map<String, CellHealthState>>(emptyMap())
    val cellHealth: StateFlow<Map<String, CellHealthState>> = _cellHealth.asStateFlow()

    init {
        createDefaultCells()
        generateDNASequences()
    }

    private fun createDefaultCells() {
        val plantCell = createCell(CellType.PLANT_CELL, 150f, 200f)
        val animalCell = createCell(CellType.ANIMAL_CELL, 350f, 200f)
        _cells.value = listOf(plantCell, animalCell)
        
        // Initialize health for cells
        val healthMap = mutableMapOf<String, CellHealthState>()
        healthMap[plantCell.id] = CellHealthState(plantCell.id)
        healthMap[animalCell.id] = CellHealthState(animalCell.id)
        _cellHealth.value = healthMap
    }

    private fun createCell(cellType: CellType, centerX: Float, centerY: Float): CellState {
        val cellId = UUID.randomUUID().toString()
        val organelles = generateOrganelles(cellType, centerX, centerY)
        
        return CellState(
            id = cellId,
            cellType = cellType,
            organelles = organelles,
            centerX = centerX,
            centerY = centerY,
            radius = 80f
        )
    }

    private fun generateOrganelles(cellType: CellType, centerX: Float, centerY: Float): List<OrganelleInstance> {
        val organelles = mutableListOf<OrganelleInstance>()
        val baseOrganelles = when (cellType) {
            CellType.PLANT_CELL -> listOf(
                Organelle.NUCLEUS,
                Organelle.CHLOROPLAST,
                Organelle.MITOCHONDRIA,
                Organelle.VACUOLE,
                Organelle.CELL_WALL,
                Organelle.GOLGI_APPARATUS
            )
            CellType.ANIMAL_CELL -> listOf(
                Organelle.NUCLEUS,
                Organelle.MITOCHONDRIA,
                Organelle.RIBOSOME,
                Organelle.ENDOPLASMIC_RETICULUM,
                Organelle.GOLGI_APPARATUS,
                Organelle.LYSOSOME,
                Organelle.CENTRIOLE,
                Organelle.VESICLE
            )
            CellType.BACTERIAL_CELL -> listOf(
                Organelle.RIBOSOME
            )
            CellType.VIRUS -> emptyList()
        }

        baseOrganelles.forEachIndexed { index, organelle ->
            val angle = (index.toFloat() / baseOrganelles.size) * 2 * Math.PI
            val distance = 40f
            val x = centerX + (distance * cos(angle)).toFloat()
            val y = centerY + (distance * sin(angle)).toFloat()

            organelles.add(
                OrganelleInstance(
                    id = UUID.randomUUID().toString(),
                    organelle = organelle,
                    x = x,
                    y = y,
                    radius = if (organelle == Organelle.NUCLEUS) 12f else 6f
                )
            )
        }

        return organelles
    }

    private fun generateDNASequences() {
        val sequences = listOf(
            DNASequence(
                id = "gene_1",
                name = "Gene 1: Hemoglobin",
                bases = generateRandomBases(50),
                description = "Codes for hemoglobin protein in red blood cells",
                geneFunction = "Oxygen transport"
            ),
            DNASequence(
                id = "gene_2",
                name = "Gene 2: Insulin",
                bases = generateRandomBases(45),
                description = "Codes for insulin hormone",
                geneFunction = "Blood glucose regulation"
            ),
            DNASequence(
                id = "gene_3",
                name = "Gene 3: Chlorophyll",
                bases = generateRandomBases(55),
                description = "Codes for chlorophyll synthesis",
                geneFunction = "Photosynthesis"
            )
        )
        _dnaSequences.value = sequences
    }

    private fun generateRandomBases(count: Int): List<DNABase> {
        val baseTypes = BaseType.values()
        val complementMap = mapOf(
            BaseType.ADENINE to BaseType.THYMINE,
            BaseType.THYMINE to BaseType.ADENINE,
            BaseType.GUANINE to BaseType.CYTOSINE,
            BaseType.CYTOSINE to BaseType.GUANINE
        )

        return (0 until count).map { position ->
            val baseType = baseTypes.random()
            DNABase(
                type = baseType,
                position = position,
                pairedWith = complementMap[baseType]
            )
        }
    }

    fun selectCellType(cellType: CellType) {
        _selectedCellType.value = cellType
    }

    fun selectCell(cellId: String?) {
        _selectedCell.value = cellId
        if (cellId != null) {
            val cell = _cells.value.find { it.id == cellId }
            if (cell != null) {
                addObservation(cell)
            }
        }
    }

    fun selectOrganelle(organelleId: String?) {
        _selectedOrganelle.value = organelleId
    }

    fun addCell() {
        val newCell = createCell(_selectedCellType.value, 150f + (_cells.value.size * 100f), 200f)
        val currentCells = _cells.value.toMutableList()
        currentCells.add(newCell)
        _cells.value = currentCells

        val healthMap = _cellHealth.value.toMutableMap()
        healthMap[newCell.id] = CellHealthState(newCell.id)
        _cellHealth.value = healthMap
    }

    fun removeCell(cellId: String) {
        _cells.value = _cells.value.filter { it.id != cellId }
        if (_selectedCell.value == cellId) {
            _selectedCell.value = null
        }

        val healthMap = _cellHealth.value.toMutableMap()
        healthMap.remove(cellId)
        _cellHealth.value = healthMap
    }

    fun setMagnification(magnification: Float) {
        _microscopeSettings.value = _microscopeSettings.value.copy(
            magnification = magnification.coerceIn(40f, 1000f)
        )
    }

    fun setBrightness(brightness: Float) {
        _microscopeSettings.value = _microscopeSettings.value.copy(
            brightness = brightness.coerceIn(0f, 1f)
        )
    }

    fun setContrast(contrast: Float) {
        _microscopeSettings.value = _microscopeSettings.value.copy(
            contrast = contrast.coerceIn(0f, 1f)
        )
    }

    fun setFocusDepth(focusDepth: Float) {
        _microscopeSettings.value = _microscopeSettings.value.copy(
            focusDepth = focusDepth.coerceIn(0f, 1f)
        )
    }

    fun setColorFilter(filter: ColorFilter) {
        _microscopeSettings.value = _microscopeSettings.value.copy(colorFilter = filter)
    }

    fun toggleGrid() {
        _microscopeSettings.value = _microscopeSettings.value.copy(
            showGrid = !_microscopeSettings.value.showGrid
        )
    }

    fun startProcess(processType: BiologicalProcess) {
        val steps = when (processType) {
            BiologicalProcess.MITOSIS -> listOf(
                "Interphase: DNA replication",
                "Prophase: Chromosomes condense",
                "Metaphase: Chromosomes align",
                "Anaphase: Chromosomes separate",
                "Telophase: Nuclear envelopes form",
                "Cytokinesis: Cell division completes"
            )
            BiologicalProcess.PHOTOSYNTHESIS -> listOf(
                "Light-dependent reactions",
                "ATP synthesis",
                "NADPH production",
                "Calvin cycle",
                "Glucose synthesis"
            )
            BiologicalProcess.PROTEIN_SYNTHESIS -> listOf(
                "Transcription: mRNA creation",
                "Translation: Protein building",
                "Ribosome assembly",
                "Amino acid chain formation",
                "Protein folding"
            )
            BiologicalProcess.DNA_REPLICATION -> listOf(
                "DNA unwinding",
                "Base pairing",
                "DNA polymerase synthesis",
                "Proofreading",
                "Replication complete"
            )
            else -> emptyList()
        }

        _currentProcess.value = CellProcess(
            processType = processType,
            progress = 0f,
            isRunning = true,
            steps = steps
        )

        animateProcess()
    }

    private fun animateProcess() {
        viewModelScope.launch {
            var progress = 0f
            while (progress < 1f && _currentProcess.value.isRunning) {
                progress += 0.02f
                _currentProcess.value = _currentProcess.value.copy(progress = progress.coerceAtMost(1f))
                kotlinx.coroutines.delay(100)
            }
            
            if (progress >= 1f) {
                _currentProcess.value = _currentProcess.value.copy(isRunning = false)
            }
        }
    }

    fun stopProcess() {
        _currentProcess.value = _currentProcess.value.copy(isRunning = false)
    }

    fun resetProcess() {
        _currentProcess.value = CellProcess(BiologicalProcess.NONE)
    }

    private fun addObservation(cell: CellState) {
        val observation = MicroscopeObservation(
            cellType = cell.cellType,
            magnification = _microscopeSettings.value.magnification,
            organellesObserved = cell.organelles.map { it.organelle.displayName }
        )

        val currentObservations = _observations.value.toMutableList()
        currentObservations.add(0, observation)
        _observations.value = currentObservations.take(10)
    }

    fun updateCellHealth(cellId: String, health: Float) {
        val healthMap = _cellHealth.value.toMutableMap()
        val currentHealth = healthMap[cellId] ?: CellHealthState(cellId)
        healthMap[cellId] = currentHealth.copy(health = health.coerceIn(0f, 100f))
        _cellHealth.value = healthMap
    }

    fun updateCellEnergy(cellId: String, energy: Float) {
        val healthMap = _cellHealth.value.toMutableMap()
        val currentHealth = healthMap[cellId] ?: CellHealthState(cellId)
        healthMap[cellId] = currentHealth.copy(energy = energy.coerceIn(0f, 100f))
        _cellHealth.value = healthMap
    }

    fun clearAll() {
        _cells.value = emptyList()
        _selectedCell.value = null
        _selectedOrganelle.value = null
        _cellHealth.value = emptyMap()
    }
}
