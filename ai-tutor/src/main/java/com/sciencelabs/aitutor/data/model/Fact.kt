package com.sciencelabs.aitutor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Fact(
    val id: String,
    val text: String,
    val source: String = ""
)
