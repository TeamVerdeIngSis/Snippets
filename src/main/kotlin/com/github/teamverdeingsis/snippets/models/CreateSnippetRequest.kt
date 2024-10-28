package com.github.teamverdeingsis.snippets.models

data class CreateSnippetRequest(
    val name: String,
    val content: String,
    val language: String,
    val extension: String)
