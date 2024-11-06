package com.github.teamverdeingsis.snippets.models


data class CreateSnippetRequest(
    val name: String,
    val content: String,
    val languageName: String,
    val languageVersion: String,
    val languageExtension: String
)

data class UpdateSnippetRequest(
    val content: String,
    val snippetId: String
)