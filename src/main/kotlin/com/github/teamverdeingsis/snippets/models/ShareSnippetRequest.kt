package com.github.teamverdeingsis.snippets.models

data class ShareSnippetRequest(
    val userId: String,       // ID del usuario que solicita compartir
    val snippetId: String,    // ID del snippet a compartir
    val fromEmail: String,    // Correo del remitente
    val toEmail: String       // Correo del destinatario
)

