package com.github.teamverdeingsis.snippets.models

data class SnippetRequest(
    val name: String,
    val description: String,
    val language: String,
    val content: String
)
