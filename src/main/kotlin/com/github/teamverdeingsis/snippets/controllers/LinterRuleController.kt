package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.producer.LinterRuleProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LinterRuleController(private val producer: LinterRuleProducer) {

    @PostMapping("/v1/linter/publish")
    suspend fun publishSnippetMessage(
        @RequestParam userId: String,
        @RequestParam snippetId: String
    ) {
        producer.publishSnippetMessage(userId, snippetId)
    }
}
