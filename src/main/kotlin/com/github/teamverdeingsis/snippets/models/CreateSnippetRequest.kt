package com.github.teamverdeingsis.snippets.models


data class CreateSnippetRequest(
    val name: String,
    val content: String,
    val languageName: String,
    val languageVersion: String,
    val languageExtension: String
)

