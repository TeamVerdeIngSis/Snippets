package com.github.teamverdeingsis.snippets.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.teamverdeingsis.snippets.factory.RulesFactory
import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.producer.LinterRuleProducer
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import org.springframework.stereotype.Service

@Service
class LintingRulesService(
    private val assetService: AssetService,
    private val snippetRepository: SnippetRepository,
    private val snippetService: SnippetService,
    private val producer: LinterRuleProducer
) {

    suspend fun modifyLintingRules(authorization: String, rules: List<Rule>): List<Rule> {
        val userId = AuthorizationDecoder.decode(authorization)

        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)

        if (assetService.assetExists("linting", userId)) {
            assetService.updateAsset(userId, "linting", rulesString)
            updateAllSnippetsStatus(authorization)
            return rules
        }
        assetService.addAsset(rulesString, "linting", userId)
        updateAllSnippetsStatus(authorization)
        return rules
    }

    fun getLintingRules(userId: String): List<Rule> {
        println("GetLintingRules checkpoint 3, llegue a getLintingRules con $userId")
        if (!assetService.assetExists("linting", userId)) {
            println("GetLintingRules checkpoint 4, no existe el asset, voy a devolver las reglas por defecto")
            val rules = RulesFactory().getDefaultLintingRules()
            println("GetLintingRules checkpoint 5, las reglas por defecto son $rules")
            return rules
        }
        println("GetLintingRules checkpoint 6, el asset existe, voy a devolver las reglas personalizadas del usuario")
        val rulesString = assetService.getAsset(userId, "linting")
        println("GetLintingRules checkpoint 7, las reglas personalizadas son $rulesString")
        val mapper = jacksonObjectMapper()
        val response = mapper.readValue(rulesString, object : TypeReference<List<Rule>>() {})
        println("GetLintingRules checkpoint 8, las reglas en el formato que necesita el front son $response")
        return response
    }

    fun updateConformance(snippetId: String, conformance: Conformance) {
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            val snippetToUpdate = snippet.get()
            snippetToUpdate.conformance = conformance
            snippetRepository.save(snippetToUpdate)
        }
    }

    suspend fun updateAllSnippetsStatus(authorization: String) {
        val userId = AuthorizationDecoder.decode(authorization)
        val username = AuthorizationDecoder.decodeUsername(authorization)

        snippetService.getAllSnippetsByUser(userId, username)?.forEach { snippet ->
            updateConformance(snippet.snippet.id, Conformance.PENDING)
            producer.publishEvent(authorization, snippet.snippet.id)
        }
    }
}