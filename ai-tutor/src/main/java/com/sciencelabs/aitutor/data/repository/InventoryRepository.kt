package com.sciencelabs.aitutor.data.repository

import com.sciencelabs.aitutor.data.model.InventoryCategory
import com.sciencelabs.aitutor.data.model.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Simple in-memory inventory repository. Pre-populated with sample lab items.
 * For persistence, this can be backed by Room in the future.
 */
class InventoryRepository {

    private val items = listOf(
        InventoryItem(id = "c1", name = "Beaker 250 mL", category = InventoryCategory.CONTAINER, description = "Glass beaker for mixing/heating.", quantity = 12),
        InventoryItem(id = "c2", name = "Erlenmeyer Flask 500 mL", category = InventoryCategory.CONTAINER, description = "Conical flask for titration and mixing.", quantity = 8),
        InventoryItem(id = "t1", name = "Pipette (10-100 µL)", category = InventoryCategory.TOOL, description = "Adjustable micropipette.", quantity = 6),
        InventoryItem(id = "t2", name = "Bunsen Burner", category = InventoryCategory.TOOL, description = "Gas flame source for heating.", quantity = 3),
        InventoryItem(id = "ch1", name = "Hydrochloric Acid (0.1M)", category = InventoryCategory.CHEMICAL, description = "Dilute HCl used for titrations.", quantity = 2),
        InventoryItem(id = "ch2", name = "Sodium Chloride (NaCl)", category = InventoryCategory.CHEMICAL, description = "Salt used in many experiments.", quantity = 5),
        InventoryItem(id = "b1", name = "E. coli culture (strain K-12)", category = InventoryCategory.BIOLOGY, description = "Non-pathogenic strain for teaching labs.", quantity = 1),
        InventoryItem(id = "b2", name = "Agar plates", category = InventoryCategory.BIOLOGY, description = "Petri dishes with nutrient agar.", quantity = 20)
    )

    private val itemsFlow = MutableStateFlow(items)

    fun getAllItems(): Flow<List<InventoryItem>> = itemsFlow

    // For potential future updates
    suspend fun addItem(item: InventoryItem) {
        val new = itemsFlow.value.toMutableList().apply { add(item) }
        itemsFlow.value = new
    }

    suspend fun updateItem(item: InventoryItem) {
        val new = itemsFlow.value.map { if (it.id == item.id) item else it }
        itemsFlow.value = new
    }

    suspend fun removeItem(itemId: String) {
        val new = itemsFlow.value.filter { it.id != itemId }
        itemsFlow.value = new
    }
}
