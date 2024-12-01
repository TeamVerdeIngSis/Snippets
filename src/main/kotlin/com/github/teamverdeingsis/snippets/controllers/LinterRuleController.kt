package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.github.teamverdeingsis.snippets.services.LintingRulesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets")
class LinterRuleController(private val lintingRulesService: LintingRulesService) {

    @GetMapping("/getLintingRules")
    fun getLintingRules(@RequestHeader("Authorization") authorization: String): ResponseEntity<List<Rule>> {
        println("GetLintingRules checkpoint 1")
        println("Llegue a /getLintingRules con $authorization")
        val userId = AuthorizationDecoder.decode(authorization)
        println("GetLintingRules checkpoint 2")
        println("El userId es $userId")
        // Delegar la operación al servicio
        return ResponseEntity.ok(lintingRulesService.getLintingRules(userId))
    }

    @PostMapping("/updateConformance")
    fun updateConformance(@RequestBody request: UpdateConformanceRequest): ResponseEntity<String> {
        lintingRulesService.updateConformance(request.snippetId, request.conformance)
        return ResponseEntity.ok("Conformance updated")
    }

    @PostMapping("/modifyLintingRules")
    suspend fun modifyLintingRules(
        @RequestBody rules: List<Rule>,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<List<Rule>> {
        println("ModifyLintingRules checkpoint 1, llegue a /modifyLintingRules con $authorization y $rules")
        return ResponseEntity.ok(lintingRulesService.modifyLintingRules(authorization, rules))
    }


}
