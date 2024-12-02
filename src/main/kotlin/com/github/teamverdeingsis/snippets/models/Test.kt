package com.github.teamverdeingsis.snippets.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class Test(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",

    @ElementCollection
    @CollectionTable(name = "test_input", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "input_value")
    var input: MutableList<String> = mutableListOf(), // Cambiar a MutableList

    @ElementCollection
    @CollectionTable(name = "test_output", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "output_value")
    var output: MutableList<String> = mutableListOf(), // Cambiar a MutableList

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    @JsonBackReference
    val snippet: Snippet,
) {
    constructor() : this(UUID.randomUUID().toString(), "", mutableListOf(), mutableListOf(), Snippet()) // Ajustar constructor también
}
