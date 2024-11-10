package com.github.teamverdeingsis.snippets.models

import com.fasterxml.jackson.databind.JsonNode


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