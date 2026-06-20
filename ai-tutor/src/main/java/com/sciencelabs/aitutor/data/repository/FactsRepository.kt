package com.sciencelabs.aitutor.data.repository

import com.sciencelabs.aitutor.data.model.Fact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Simple in-memory facts repository. Holds sample facts and favorite state.
 * Can be replaced by a network-backed or Room-backed implementation later.
 */
class FactsRepository {

    private val sampleFacts = listOf(
        Fact(id = "f1", text = "Water expands when it freezes, which is why ice floats on liquid water.", source = "Physics/Chemistry"),
        Fact(id = "f2", text = "DNA was first isolated by Friedrich Miescher in 1869.", source = "Biology"),
        Fact(id = "f3", text = "Light from the Sun takes about 8 minutes and 20 seconds to reach Earth.", source = "Astronomy"),
        Fact(id = "f4", text = "The periodic table arranges elements by increasing atomic number and recurring chemical properties.", source = "Chemistry"),
        Fact(id = "f5", text = "Plants convert sunlight into chemical energy using photosynthesis in chloroplasts.", source = "Biology"),
        Fact(id = "f6", text = "Absolute zero is the temperature at which particles have minimum thermal motion (~0 K).", source = "Physics"),
        Fact(id = "f7", text = "Bacteria can swap genes through horizontal gene transfer, speeding up adaptation.", source = "Biology"),
        Fact(id = "f8", text = "A mole is Avogadro's number: approximately 6.022×10^23 particles.", source = "Chemistry")
    )

    private val _currentFact = MutableStateFlow(sampleFacts.random())
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())

    val currentFact: StateFlow<Fact> = _currentFact.asStateFlow()
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    fun refreshFact() {
        // pick a random fact different from current when possible
        if (sampleFacts.size <= 1) return
        var next: Fact
        do {
            next = sampleFacts[Random.nextInt(sampleFacts.size)]
        } while (next.id == _currentFact.value.id)
        _currentFact.value = next
    }

    fun toggleFavorite(factId: String) {
        val set = _favorites.value.toMutableSet()
        if (set.contains(factId)) set.remove(factId) else set.add(factId)
        _favorites.value = set
    }

    fun isFavorite(factId: String): Boolean = _favorites.value.contains(factId)
}
