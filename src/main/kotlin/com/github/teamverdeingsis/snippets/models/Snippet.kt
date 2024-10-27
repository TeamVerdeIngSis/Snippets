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
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String,
    val author: String,
    val conformance: Conformance,
    var assetId: String,

    @ManyToOne
    @JoinColumn(name = "language_id")
    val language: Language
) {
    constructor(): this(0, "", "", Conformance.PENDING, "", Language(0, "", "", "", emptyList()))
}

enum class Conformance {
    PENDING,
    COMPLIANT,
    NOT_COMPLIANT
}

@Entity
data class Language(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val name: String,
    val version: String,
    val extension: String,

    @OneToMany(mappedBy = "language")
    val snippets: List<Snippet>
) {
    constructor(): this(null, "", "", "", emptyList())
}
