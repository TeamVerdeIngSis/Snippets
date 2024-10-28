package com.github.teamverdeingsis.snippets.models

data class SnippetRequest(
    val name: String,
    val description: String,
    val language: String,
    val version: String,
    val extension: String,
    val content: String
)
