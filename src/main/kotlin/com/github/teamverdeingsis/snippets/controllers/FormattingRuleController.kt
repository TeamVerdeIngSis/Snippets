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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets")
class FormattingRuleController(private val formattingRulesService: FormattingRulesService) {

    @GetMapping("/getFormattingRules")
    fun getFormattingRules(@RequestHeader("Authorization") authorization: String): ResponseEntity<List<Rule>> {
        println("GetFormattingRules checkpoint 1")
        println("Llegue a /getFormattingRules con $authorization")
        val userId = AuthorizationDecoder.decode(authorization)
        println("GetFormattingRules checkpoint 2, el userId es $userId")
        return ResponseEntity.ok(formattingRulesService.getFormattingRules(userId))
    }


    @PostMapping("/modifyFormattingRules")
    suspend fun modifyFormattingRules(
        @RequestBody rules: List<Rule>,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<List<Rule>> {
        println("ModifyFormattingRules checkpoint 1, llegue a /modifyFormattingRules con $rules y $authorization")
        return ResponseEntity.ok(formattingRulesService.modifyFormattingRules(authorization, rules))
    }

    @PostMapping("/formatSnippet")
    fun formatSnippet(@RequestBody request: FormatSnippetRequest, @RequestHeader authorization: String): ResponseEntity<String> {
        if (request.snippetId.isEmpty() || request.content.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request body")
        }
        val formattedCode = formattingRulesService.formatSnippet(request, authorization)
        return ResponseEntity.ok(formattedCode)
    }
}