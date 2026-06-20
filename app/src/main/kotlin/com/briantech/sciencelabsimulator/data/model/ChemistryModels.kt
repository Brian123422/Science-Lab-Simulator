package com.briantech.sciencelabsimulator.data.model

import androidx.compose.ui.graphics.Color

// Container Types
enum class ContainerType {
    TEST_TUBE_SMALL,
    TEST_TUBE_MEDIUM,
    TEST_TUBE_LARGE,
    BEAKER_SMALL,
    BEAKER_MEDIUM,
    BEAKER_LARGE,
    FLASK_SMALL,
    FLASK_MEDIUM,
    FLASK_LARGE,
    GRADUATED_CYLINDER_SMALL,
    GRADUATED_CYLINDER_MEDIUM,
    GRADUATED_CYLINDER_LARGE,
    PETRI_DISH_SMALL,
    PETRI_DISH_MEDIUM,
    PETRI_DISH_LARGE
}

// Liquid Types with Properties
enum class LiquidType(val displayName: String, val color: Color, val density: Float, val acidity: Float) {
    WATER("Water", Color(0xFF4A90E2), 1.0f, 7.0f),
    OIL("Oil", Color(0xFFD4AF37), 0.9f, 7.0f),
    VINEGAR("Vinegar", Color(0xFFFFD700), 1.01f, 2.5f),
    ALCOHOL("Alcohol", Color(0xFFC0C0C0), 0.79f, 7.0f),
    ACID("Acid", Color(0xFFFF6B6B), 1.1f, 1.0f),
    BASE("Base", Color(0xFF00D4FF), 1.05f, 13.0f),
    SALT("Salt", Color(0xFFF5DEB3), 2.16f, 7.0f),
    MYSTERY("Mystery", Color(0xFF9D4EDD), 1.2f, 7.0f)
}

// Container State
data class Container(
    val id: String,
    val type: ContainerType,
    val position: Pair<Float, Float>, // x, y coordinates
    val capacity: Float, // ml
    val currentVolume: Float = 0f, // ml
    val liquids: List<ContainerLiquid> = emptyList(),
    val isSelected: Boolean = false,
    val temperature: Float = 20f, // Celsius
    val isReacting: Boolean = false
)

data class ContainerLiquid(
    val liquidType: LiquidType,
    val volume: Float, // ml
    val color: Color = liquidType.color
)

// Reaction Data
data class ChemicalReaction(
    val liquid1: LiquidType,
    val liquid2: LiquidType,
    val product: LiquidType?,
    val productVolume: Float,
    val reactionColor: Color,
    val temperature: Float,
    val hasGas: Boolean = false,
    val isBubbling: Boolean = false,
    val description: String
)

// Reaction Rules
object ReactionRules {
    private val reactions = mapOf(
        // Acid + Base = Salt + Water
        Pair(LiquidType.ACID, LiquidType.BASE) to ChemicalReaction(
            liquid1 = LiquidType.ACID,
            liquid2 = LiquidType.BASE,
            product = LiquidType.SALT,
            productVolume = 1.8f,
            reactionColor = Color(0xFFFFA500),
            temperature = 35f,
            hasGas = true,
            isBubbling = true,
            description = "Acid-Base neutralization: Bubbling and heat generation!"
        ),
        // Vinegar + Base
        Pair(LiquidType.VINEGAR, LiquidType.BASE) to ChemicalReaction(
            liquid1 = LiquidType.VINEGAR,
            liquid2 = LiquidType.BASE,
            product = null,
            productVolume = 1.5f,
            reactionColor = Color(0xFFFF1744),
            temperature = 28f,
            hasGas = true,
            isBubbling = true,
            description = "Vinegar reacts with base: Fizzing reaction!"
        ),
        // Oil + Water (no reaction, layering)
        Pair(LiquidType.OIL, LiquidType.WATER) to ChemicalReaction(
            liquid1 = LiquidType.OIL,
            liquid2 = LiquidType.WATER,
            product = null,
            productVolume = 2.0f,
            reactionColor = Color(0xFFD4AF37),
            temperature = 20f,
            hasGas = false,
            isBubbling = false,
            description = "Oil and water don't mix: Layering effect!"
        ),
        // Acid + Oil
        Pair(LiquidType.ACID, LiquidType.OIL) to ChemicalReaction(
            liquid1 = LiquidType.ACID,
            liquid2 = LiquidType.OIL,
            product = null,
            productVolume = 1.9f,
            reactionColor = Color(0xFF8B4513),
            temperature = 45f,
            hasGas = false,
            isBubbling = false,
            description = "Acid reacts with oil: Heat generation!"
        ),
        // Mystery + Anything
        Pair(LiquidType.MYSTERY, LiquidType.WATER) to ChemicalReaction(
            liquid1 = LiquidType.MYSTERY,
            liquid2 = LiquidType.WATER,
            product = null,
            productVolume = 2.0f,
            reactionColor = Color(0xFF00FF00),
            temperature = 30f,
            hasGas = true,
            isBubbling = true,
            description = "Mystery compound reacts: Green luminescence!"
        )
    )

    fun getReaction(liquid1: LiquidType, liquid2: LiquidType): ChemicalReaction? {
        return reactions[Pair(liquid1, liquid2)] ?: reactions[Pair(liquid2, liquid1)]
    }
}

// Container Size Properties
data class ContainerProperties(
    val width: Float,
    val height: Float,
    val capacity: Float,
    val displayName: String
)

fun getContainerProperties(type: ContainerType): ContainerProperties {
    return when (type) {
        // Test Tubes
        ContainerType.TEST_TUBE_SMALL -> ContainerProperties(30f, 100f, 25f, "Small Test Tube")
        ContainerType.TEST_TUBE_MEDIUM -> ContainerProperties(35f, 130f, 50f, "Medium Test Tube")
        ContainerType.TEST_TUBE_LARGE -> ContainerProperties(40f, 160f, 100f, "Large Test Tube")
        // Beakers
        ContainerType.BEAKER_SMALL -> ContainerProperties(50f, 70f, 50f, "Small Beaker")
        ContainerType.BEAKER_MEDIUM -> ContainerProperties(65f, 90f, 150f, "Medium Beaker")
        ContainerType.BEAKER_LARGE -> ContainerProperties(80f, 110f, 250f, "Large Beaker")
        // Flasks
        ContainerType.FLASK_SMALL -> ContainerProperties(45f, 85f, 50f, "Small Flask")
        ContainerType.FLASK_MEDIUM -> ContainerProperties(60f, 110f, 150f, "Medium Flask")
        ContainerType.FLASK_LARGE -> ContainerProperties(75f, 140f, 250f, "Large Flask")
        // Graduated Cylinders
        ContainerType.GRADUATED_CYLINDER_SMALL -> ContainerProperties(25f, 120f, 50f, "Small Graduated Cylinder")
        ContainerType.GRADUATED_CYLINDER_MEDIUM -> ContainerProperties(30f, 150f, 100f, "Medium Graduated Cylinder")
        ContainerType.GRADUATED_CYLINDER_LARGE -> ContainerProperties(35f, 180f, 200f, "Large Graduated Cylinder")
        // Petri Dishes
        ContainerType.PETRI_DISH_SMALL -> ContainerProperties(60f, 25f, 30f, "Small Petri Dish")
        ContainerType.PETRI_DISH_MEDIUM -> ContainerProperties(80f, 30f, 50f, "Medium Petri Dish")
        ContainerType.PETRI_DISH_LARGE -> ContainerProperties(100f, 35f, 80f, "Large Petri Dish")
    }
}
