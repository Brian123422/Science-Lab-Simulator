package com.briantech.sciencelabsimulator.data.model

import androidx.compose.ui.graphics.Color

// Cell Types
enum class CellType {
    PLANT_CELL,
    ANIMAL_CELL,
    BACTERIAL_CELL,
    VIRUS
}

// Cell Organelles
enum class Organelle(val displayName: String, val description: String, val color: Color) {
    NUCLEUS("Nucleus", "Contains genetic material (DNA/RNA)", Color(0xFF8B008B)),
    MITOCHONDRIA("Mitochondria", "Powerhouse of the cell - produces ATP", Color(0xFFFF4500)),
    RIBOSOME("Ribosome", "Synthesizes proteins", Color(0xFFFFD700)),
    ENDOPLASMIC_RETICULUM("Endoplasmic Reticulum", "Synthesizes and transports proteins", Color(0xFF87CEEB)),
    GOLGI_APPARATUS("Golgi Apparatus", "Modifies and packages proteins", Color(0xFF98FB98)),
    LYSOSOME("Lysosome", "Breaks down waste materials", Color(0xFFFF6B6B)),
    CHLOROPLAST("Chloroplast", "Performs photosynthesis (Plants only)", Color(0xFF00FF00)),
    CELL_WALL("Cell Wall", "Provides structural support (Plants only)", Color(0xFFD2691E)),
    VACUOLE("Vacuole", "Stores water and nutrients", Color(0xFF4169E1)),
    CYTOPLASM("Cytoplasm", "Gel-like substance filling the cell", Color(0xFFFFF8DC)),
    CENTRIOLE("Centriole", "Involved in cell division (Animals only)", Color(0xFFFF1493)),
    VESICLE("Vesicle", "Transports molecules within the cell", Color(0xFFFFB6C1))
}

// Organelle Position and State
data class OrganelleInstance(
    val id: String,
    val organelle: Organelle,
    val x: Float,
    val y: Float,
    val radius: Float,
    val isSelected: Boolean = false,
    val isAnimating: Boolean = false
)

// Cell State
data class CellState(
    val id: String,
    val cellType: CellType,
    val organelles: List<OrganelleInstance>,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val isSelected: Boolean = false,
    val rotationAngle: Float = 0f,
    val zoom: Float = 1f,
    val health: Float = 100f,
    val isAlive: Boolean = true
)

// DNA Structure
data class DNABase(
    val type: BaseType,
    val position: Int,
    val pairedWith: BaseType?
)

enum class BaseType(val color: Color, val letter: String) {
    ADENINE(Color(0xFF00AA00), "A"),
    THYMINE(Color(0xFFAA00AA), "T"),
    GUANINE(Color(0xFFFF8800), "G"),
    CYTOSINE(Color(0xFF0088FF), "C")
}

// DNA Sequence
data class DNASequence(
    val id: String,
    val name: String,
    val bases: List<DNABase>,
    val description: String = "",
    val geneFunction: String = "",
    val scrollPosition: Int = 0
)

// Chromosome
data class Chromosome(
    val id: String,
    val number: Int,
    val sequences: List<DNASequence>,
    val position: Pair<Float, Float>,
    val isSelected: Boolean = false
)

// Microscope Settings
data class MicroscopeSettings(
    val magnification: Float = 400f,  // 40x to 1000x
    val brightness: Float = 0.5f,     // 0 to 1
    val contrast: Float = 0.5f,       // 0 to 1
    val focusDepth: Float = 0.5f,     // 0 to 1
    val temperature: Float = 37f,     // Celsius (for live cells)
    val showGrid: Boolean = false,
    val showScale: Boolean = true,
    val colorFilter: ColorFilter = ColorFilter.NORMAL
)

enum class ColorFilter {
    NORMAL,
    GRAYSCALE,
    HEAT_MAP,
    FLUORESCENCE,
    PHASE_CONTRAST
}

// Biological Process Animation
enum class BiologicalProcess {
    MITOSIS,
    MEIOSIS,
    PHOTOSYNTHESIS,
    RESPIRATION,
    PROTEIN_SYNTHESIS,
    DNA_REPLICATION,
    NONE
}

data class CellProcess(
    val processType: BiologicalProcess,
    val progress: Float = 0f,  // 0 to 1
    val isRunning: Boolean = false,
    val description: String = "",
    val steps: List<String> = emptyList()
)

// Cell Health Indicators
data class CellHealthState(
    val cellId: String,
    val health: Float = 100f,       // 0-100
    val energy: Float = 100f,       // 0-100
    val temperature: Float = 37f,   // Celsius
    val ph: Float = 7.4f,           // pH level
    val metabolism: Float = 1.0f,   // Metabolic rate
    val stressLevel: Float = 0f     // 0-100
)

// Microscope Observation Data
data class MicroscopeObservation(
    val timestamp: Long = System.currentTimeMillis(),
    val cellType: CellType,
    val magnification: Float,
    val organellesObserved: List<String>,
    val notes: String = "",
    val imageUrl: String = ""
)

// Sample Slide
data class BiologySample(
    val id: String,
    val name: String,
    val description: String,
    val cellTypes: List<CellType>,
    val organellesVisible: List<Organelle>,
    val dnaSequences: List<DNASequence> = emptyList(),
    val difficulty: String = "Medium"
)

// Predefined Samples
fun getSampleSlides(): List<BiologySample> {
    return listOf(
        BiologySample(
            id = "plant_cell_1",
            name = "Plant Leaf Cell",
            description = "Cross-section of a typical plant leaf cell with chloroplasts",
            cellTypes = listOf(CellType.PLANT_CELL),
            organellesVisible = listOf(
                Organelle.NUCLEUS,
                Organelle.CHLOROPLAST,
                Organelle.MITOCHONDRIA,
                Organelle.VACUOLE,
                Organelle.CELL_WALL
            )
        ),
        BiologySample(
            id = "animal_cell_1",
            name = "Human Cheek Cell",
            description = "Sample of human epithelial cells from cheek tissue",
            cellTypes = listOf(CellType.ANIMAL_CELL),
            organellesVisible = listOf(
                Organelle.NUCLEUS,
                Organelle.MITOCHONDRIA,
                Organelle.RIBOSOME,
                Organelle.GOLGI_APPARATUS,
                Organelle.CENTRIOLE
            )
        ),
        BiologySample(
            id = "bacteria_1",
            name = "E. Coli Bacteria",
            description = "Bacterial cell showing prokaryotic structure",
            cellTypes = listOf(CellType.BACTERIAL_CELL),
            organellesVisible = listOf(Organelle.RIBOSOME)
        )
    )
}
