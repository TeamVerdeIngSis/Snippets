package com.github.teamverdeingsis.snippets.models

data class TestRequest(
    val name: String,
    val input: List<String>,
    val output: List<String>,
)
