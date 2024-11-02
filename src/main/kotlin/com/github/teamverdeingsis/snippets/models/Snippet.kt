package com.github.teamverdeingsis.snippets.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    val name: String,
    val userId: String,
    var conformance: Conformance = Conformance.PENDING,
    var assetId: String,

    @ManyToOne
    @JoinColumn(name = "language_name", referencedColumnName = "name")
    val language: Language
) {
    constructor() : this(
        name = "",
        userId = "",
        assetId = "",
        language = Language("", "", "")
    ) {

    }
}

enum class Conformance {
    PENDING,
    COMPLIANT,
    NOT_COMPLIANT
}


@Entity
data class Language(
    @Id
    val name: String,
    val version: String,
    val extension: String,

    @OneToMany(mappedBy = "language")
    val snippets: List<Snippet> = emptyList()
) {
    constructor() : this(
        name = "",
        version = "",
        extension = ""
    ) {

    }
}
