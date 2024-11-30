package com.github.teamverdeingsis.snippets.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.teamverdeingsis.snippets.factory.RulesFactory
import com.github.teamverdeingsis.snippets.models.FormatSnippetRequest
import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.producer.FormattingRuleProducer
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import org.springframework.stereotype.Service

@Service
class FormattingRulesService(
    private val assetService: AssetService,
    private val snippetService: SnippetService,
    private val producer: FormattingRuleProducer,
    private val parseService: ParseService
) {

    /**
     * Modifica las reglas de formateo existentes o crea un nuevo asset si no existen.
     */
    suspend fun modifyFormattingRules(authorization: String, rules: List<Rule>): List<Rule> {
        val userId = AuthorizationDecoder.decode(authorization)

        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)

        if (assetService.assetExists("format", userId)) {
            assetService.updateAsset(userId, "format", rulesString)
        } else {
            assetService.addAsset(rulesString, "format", userId)
        }
        val result = assetService.getAsset(userId, "format")
        updateFormatOfSnippets(authorization)
        return rules
    }

    /**
     * Obtiene las reglas de formateo del usuario. Si no existen, devuelve las reglas predeterminadas.
     */
    fun getFormattingRules(userId: String): List<Rule> {
        println("GetFormattingRules checkpoint 3, llegue a getFormattingRules con $userId")
        if (!assetService.assetExists("format", userId)) {
            println("GetFormattingRules checkpoint 4, no existe el asset, voy a devolver las reglas por defecto")
            val rules = RulesFactory().getDefaultFormattingRules()
            println("GetFormattingRules checkpoint 5, las reglas por defecto son $rules")
            return rules
        }
        println("GetFormattingRules checkpoint 6, el asset existe, voy a devolver las reglas personalizadas del usuario")
        val rulesString = assetService.getAsset(userId, "format")
        println("GetFormattingRules checkpoint 7, las reglas personalizadas son $rulesString")
        val mapper = jacksonObjectMapper()
        val rules = mapper.readValue(rulesString, object : TypeReference<List<Rule>>() {})
        println("GetFormattingRules checkpoint 8, las reglas en el formato que necesita el front son $rules")
        return rules
    }

    /**
     * Llama al servicio Parse para delegar el formateo del snippet.
     */
    fun formatSnippet(snippetContent: FormatSnippetRequest, authorization: String): String? {
        return parseService.formatSnippet(snippetContent, authorization)
    }

    /**
     * Publica eventos para re-formatear todos los snippets del usuario.
     */
    suspend fun updateFormatOfSnippets(authorization: String) {
        val userId = AuthorizationDecoder.decode(authorization)
        val username = AuthorizationDecoder.decodeUsername(authorization)

        snippetService.getAllSnippetsByUser(userId, username)?.forEach { snippet ->
            producer.publishEvent(authorization, snippet.snippet.id)
        }
    }
}