package com.github.teamverdeingsis.snippets.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column

@Entity
data class Test(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: String = "",
    val name: String = "",

    @ElementCollection
    @CollectionTable(name = "test_input", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "input_value")
    val input: List<String> = listOf(),

    @ElementCollection
    @CollectionTable(name = "test_output", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "output_value")
    val output: List<String> = listOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    @JsonBackReference
    val snippet: Snippet
) {
    constructor() : this("", "", listOf(), listOf(), Snippet())
}