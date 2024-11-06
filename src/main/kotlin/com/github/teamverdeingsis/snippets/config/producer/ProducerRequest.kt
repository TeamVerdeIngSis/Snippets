package com.github.teamverdeingsis.snippets.config.producer

import java.util.UUID

data class ProducerRequest(
    val snippetId: UUID,
    val userId: String,
)