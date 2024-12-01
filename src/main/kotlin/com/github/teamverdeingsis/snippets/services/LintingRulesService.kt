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
        println("ModifyLintingRules checkpoint 2, llegue a modifyLintingRules")
        val userId = AuthorizationDecoder.decode(authorization)
        println("ModifyLintingRules checkpoint 3, el userId es $userId")
        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)
        println("ModifyLintingRules checkpoint 4, las reglas en formato string son $rulesString")
        if (assetService.assetExists("linting", userId)) {
            println("ModifyLintingRules checkpoint 5, el asset existe, voy a actualizar las reglas")
            assetService.updateAsset(userId, "linting", rulesString)
            println("ModifyLintingRules checkpoint 6, actualice las reglas, voy a actualizar el estado de todos los snippets")
            updateAllSnippetsStatus(authorization)
            println("ModifyLintingRules checkpoint 7, actualice el estado de todos los snippets a PENDING")
            return rules
        }
        println("ModifyLintingRules checkpoint 8, el asset no existe, voy a agregar las reglas")
        assetService.addAsset(rulesString, "linting", userId)
        println("ModifyLintingRules checkpoint 9, agregue las reglas, voy a actualizar el estado de todos los snippets")
        updateAllSnippetsStatus(authorization)
        println("ModifyLintingRules checkpoint 10, actualice el estado de todos los snippets a PENDING")
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
        println("UpdateConformance checkpoint 1, llegue a updateConformance")
        val snippet = snippetRepository.findById(snippetId)
        println("UpdateConformance checkpoint 2, el snippet es $snippet")
        if (snippet.isPresent) {
            val snippetToUpdate = snippet.get()
            snippetToUpdate.conformance = conformance
            snippetRepository.save(snippetToUpdate)
            println("UpdateConformance checkpoint 3, actualice el conformance del snippet")
        }

    }

    suspend fun updateAllSnippetsStatus(authorization: String) {
        println("UpdateAllSnippetsStatus checkpoint 1, llegue a updateAllSnippetsStatus")
        val userId = AuthorizationDecoder.decode(authorization)
        val username = AuthorizationDecoder.decodeUsername(authorization)
        println("UpdateAllSnippetsStatus checkpoint 2, el userId es $userId y el username es $username")
        snippetService.getAllSnippetsByUser(userId, username)?.forEach { snippet ->
            println("UpdateAllSnippetsStatus checkpoint 3, voy a actualizar el snippet ${snippet.snippet.id}")
            updateConformance(snippet.snippet.id, Conformance.PENDING)
            producer.publishEvent(authorization, snippet.snippet.id)
        }
    }
}