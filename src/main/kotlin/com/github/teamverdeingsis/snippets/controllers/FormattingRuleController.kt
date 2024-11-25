package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.FormatSnippetRequest
import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.github.teamverdeingsis.snippets.services.FormattingRulesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class FormattingRuleController(private val formattingRulesService: FormattingRulesService) {

    @GetMapping("/getFormattingRules")
    fun getFormattingRules(@RequestHeader("Authorization") authorization: String): ResponseEntity<List<Rule>> {

        val userId = AuthorizationDecoder.decode(authorization)
        return ResponseEntity.ok(formattingRulesService.getFormattingRules(userId))
    }


    @PostMapping("/modifyFormattingRules")
    suspend fun modifyFormattingRules(
        @RequestBody rules: List<Rule>,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<List<Rule>> {
        println("llegue al controller con ${rules.size} reglas y $authorization")
        return ResponseEntity.ok(formattingRulesService.modifyFormattingRules(authorization, rules))
    }

    @PostMapping("/formatSnippet")
    fun formatSnippet(@RequestBody request: FormatSnippetRequest, @RequestHeader authorization: String): ResponseEntity<String> {
        println("Llegue al formato con: snippetId=${request.snippetId}, content=${request.content}, authorization=$authorization")
        if (request.snippetId.isEmpty() || request.content.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request body")
        }
        val formattedCode = formattingRulesService.formatSnippet(request, authorization)
        return ResponseEntity.ok(formattedCode)
    }
}