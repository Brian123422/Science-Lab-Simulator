package com.sciencelabs.aitutor.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class InventoryCategory {
    CONTAINER,
    TOOL,
    CHEMICAL,
    BIOLOGY
}

@Serializable
data class InventoryItem(
    val id: String,
    val name: String,
    val category: InventoryCategory,
    val description: String = "",
    val quantity: Int = 0
)
