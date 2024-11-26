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
        println("voy a modificar las reglas")
        val userId = AuthorizationDecoder.decode(authorization)

        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)

        if (assetService.assetExists("linting", userId)) {
            assetService.updateAsset(userId, "linting", rulesString)
            println("actualice las reglas de linting")
            updateAllSnippetsStatus(authorization)
            return rules
        }
        assetService.addAsset(rulesString, "linting", userId)
        println("agregue las reglas de linting")
        updateAllSnippetsStatus(authorization)
        return rules
    }

    fun getLintingRules(userId: String): List<Rule> {
        if (!assetService.assetExists("linting", userId)) {
            return RulesFactory().getDefaultLintingRules()
        }
        val rulesString = assetService.getAsset(userId, "linting")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(rulesString, object : TypeReference<List<Rule>>() {})
    }

    fun updateConformance(snippetId: String, conformance: Conformance) {
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            val snippetToUpdate = snippet.get()
            snippetToUpdate.conformance = conformance
            snippetRepository.save(snippetToUpdate)
        }
    }

    private suspend fun updateAllSnippetsStatus(authorization: String) {
        println("voy a actualizar todos los snippets a pending")
        val userId = AuthorizationDecoder.decode(authorization)
        val username = AuthorizationDecoder.decodeUsername(authorization)

        println("voy a publicar")
        snippetService.getAllSnippetsByUser(userId, username)?.forEach { snippet ->
            updateConformance(snippet.snippet.id, Conformance.PENDING)
            producer.publishEvent(authorization, snippet.snippet.id)
        }
        println("publiqué")
    }
}