package com.github.teamverdeingsis.snippets.exceptions

class SnippetNotFoundException(id: String) : RuntimeException("Snippet with ID $id not found")
