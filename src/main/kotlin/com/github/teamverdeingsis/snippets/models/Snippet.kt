package com.github.teamverdeingsis.snippets.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Snippet(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val userId: String,
    var conformance: Conformance = Conformance.PENDING,
    val languageName: String,
    val languageExtension: String,
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        name = "",
        userId = "",
        languageName = "",
        languageExtension = "",
    )
}

enum class Conformance {
    PENDING,
    COMPLIANT,
    NOT_COMPLIANT,
}
