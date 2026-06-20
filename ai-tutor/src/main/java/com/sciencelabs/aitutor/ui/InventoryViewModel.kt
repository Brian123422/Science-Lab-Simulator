package com.sciencelabs.aitutor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciencelabs.aitutor.data.model.InventoryCategory
import com.sciencelabs.aitutor.data.model.InventoryItem
import com.sciencelabs.aitutor.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedCategories = MutableStateFlow(setOf<InventoryCategory>())

    private val allItemsFlow = repository.getAllItems()

    val items: StateFlow<List<InventoryItem>> = combine(allItemsFlow, query, selectedCategories) { items, q, cats ->
        val filtered = items.filter { item ->
            val matchesQuery = q.isBlank() || item.name.contains(q, ignoreCase = true) || item.description.contains(q, ignoreCase = true)
            val matchesCategory = cats.isEmpty() || cats.contains(item.category)
            matchesQuery && matchesCategory
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setQuery(q: String) {
        query.value = q
    }

    fun toggleCategory(cat: InventoryCategory) {
        val set = selectedCategories.value.toMutableSet()
        if (set.contains(cat)) set.remove(cat) else set.add(cat)
        selectedCategories.value = set
    }

    fun isCategorySelected(cat: InventoryCategory): Boolean = selectedCategories.value.contains(cat)

    fun clearFilters() {
        selectedCategories.value = emptySet()
        query.value = ""
    }

    // convenience wrappers to allow UI-driven updates
    fun addSampleItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }
}
