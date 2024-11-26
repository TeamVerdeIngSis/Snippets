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
        println("Modificando las reglas de formateo")
        val userId = AuthorizationDecoder.decode(authorization)

        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)

        if (assetService.assetExists("format", userId)) {
            println("El asset de reglas ya existe, actualizando...")
            assetService.updateAsset(userId, "format", rulesString)
            println("Reglas de formateo actualizadas correctamente")
        } else {
            println("El asset de reglas no existe, creando uno nuevo...")
            assetService.addAsset(rulesString, "format", userId)
            println("Reglas de formateo creadas correctamente")
        }
        val result = assetService.getAsset(userId, "format")
        println("Reglas de formateo despues de actualizarlas: $result")
        updateFormatOfSnippets(authorization)
        return rules
    }

    /**
     * Obtiene las reglas de formateo del usuario. Si no existen, devuelve las reglas predeterminadas.
     */
    fun getFormattingRules(userId: String): List<Rule> {
        if (!assetService.assetExists("format", userId)) {
            println("No se encontraron reglas para el usuario, devolviendo reglas predeterminadas")
            return RulesFactory().getDefaultFormattingRules()
        }
        val rulesString = assetService.getAsset(userId, "format")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(rulesString, object : TypeReference<List<Rule>>() {})
    }

    /**
     * Llama al servicio Parse para delegar el formateo del snippet.
     */
    fun formatSnippet(snippetContent: FormatSnippetRequest, authorization: String): String? {
        println("Delegando el formateo al ParseService")
        return parseService.formatSnippet(snippetContent, authorization)
    }

    /**
     * Publica eventos para re-formatear todos los snippets del usuario.
     */
    private suspend fun updateFormatOfSnippets(authorization: String) {
        println("Actualizando el formato de todos los snippets")
        val userId = AuthorizationDecoder.decode(authorization)
        val username = AuthorizationDecoder.decodeUsername(authorization)

        snippetService.getAllSnippetsByUser(userId, username)?.forEach { snippet ->
            println("Publicando evento para formatear el snippet con ID: ${snippet.snippet.id}")
            producer.publishEvent(authorization, snippet.snippet.id)
        }
        println("Eventos publicados exitosamente")
    }
}