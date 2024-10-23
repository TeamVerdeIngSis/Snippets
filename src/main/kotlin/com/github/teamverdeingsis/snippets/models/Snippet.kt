package com.github.teamverdeingsis.snippets.models

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Snippet(
    @Id
    val id: String? = null,
    var name: String,
    var description: String,
    var language: String,
    var assetId: String
)
