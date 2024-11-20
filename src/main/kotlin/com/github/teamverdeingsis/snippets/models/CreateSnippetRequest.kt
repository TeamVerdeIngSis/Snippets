package com.github.teamverdeingsis.snippets.models

import com.fasterxml.jackson.databind.JsonNode
import java.util.*


data class CreateSnippetRequest(
    val name: String,
    val content: String,
    val language: String,
    val extension: String
)

data class UpdateSnippetRequest(
    val content: String,
    val snippetId: String
)

data class CreatePermissionRequest(
    val userId: String,
    val snippetId: String,
    val permission: String
)

data class ShareSnippetRequest(
    val userId: String,
    val snippetId: String
)

data class Permission(
    val userId: String,
    val snippetId: String,
    val permission: String,
    val permissionId: String
)
data class RulesRequest(
    val userId: String,
    val rules: JsonNode
)

data class FullSnippet(
    val id: String,
    val name: String,
    val userId: String,
    var conformance: Conformance = Conformance.PENDING,
    val languageName: String,
    val languageExtension: String,
    val content: String
)