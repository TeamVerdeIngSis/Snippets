package com.github.teamverdeingsis.snippets.models

data class TestResponse(
    val id: String,
    val name: String,
    val input: List<String>,
    val output: List<String>,
    val message: String,
)
