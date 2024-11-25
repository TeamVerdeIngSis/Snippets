package com.github.teamverdeingsis.snippets.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.teamverdeingsis.snippets.factory.RulesFactory
import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FullSnippet
import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.nimbusds.jwt.JWTParser
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.http.HttpHeaders
import kotlin.jvm.optionals.getOrNull

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionsService: PermissionsSerivce,
    private val assetService: AssetService,
    private val parseService: ParseService
) {

    fun createSnippet(createSnippetRequest: CreateSnippetRequest, authorization: String): Snippet {

        val userId = AuthorizationDecoder.decode(authorization)

        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = userId,
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension
        )
        snippetRepository.save(snippet)
        assetService.addAsset(createSnippetRequest.content, "snippets", snippet.id)
        permissionsService.addPermission(userId, snippet.id, "WRITE")
        parseService.lintSnippet(snippet.id, authorization)
        return snippet
    }

    fun helloParse(): ResponseEntity<String> {
        val response = parseService.hey()
        return ResponseEntity.ok(response)
    }

    fun helloPermissions(): ResponseEntity<String> {
        val response = permissionsService.hey()
        return ResponseEntity.ok(response)
    }

    fun shareSnippet(shareSnippetRequest: ShareSnippetRequest): String {
        return permissionsService.addPermission(shareSnippetRequest.userId, shareSnippetRequest.snippetId, "READ")
    }

    fun delete(id: String): String? {
        val snippet = snippetRepository.findById(id).getOrNull()
        if (snippet == null) {
            println("Nothing to delete")
            return null
        }
        snippetRepository.delete(snippet)
        return assetService.deleteAsset(id, "snippets").body
    }

    fun updateSnippet(id: String, content: String): String? {
        return assetService.updateAsset(id, "snippets", content).body
    }

    fun getSnippet(id: String): Snippet? {
        val snippet = snippetRepository.findById(id).getOrNull()
        return snippet
    }

    fun getSnippetWithContent(id: String): FullSnippet? {
        val content = assetService.getAsset(id, "snippets")
        val snippet = snippetRepository.findById(id).getOrNull() ?: return null
        return FullSnippet(
            id = snippet.id,
            name = snippet.name,
            userId = snippet.userId,
            conformance = snippet.conformance,
            languageName = snippet.languageName,
            languageExtension = snippet.languageExtension,
            content = content ?: ""
        )
    }

    fun getAllSnippetsByUser(userId: String): List<Snippet>? {
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        val snippets = ArrayList<Snippet>()
        if (snippetsID == null) {
            return emptyList()
        }
        for (id in snippetsID) {
            val snippet = getSnippet(id.snippetId)
            snippets.add(snippet ?: continue)
        }
        return snippets
    }

    fun validateSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.validateSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Validation failed")
    }

    fun executeSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.executeSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Execution failed")
    }

    fun formatSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.formatSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Formatting failed")
    }

    fun analyzeSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.analyzeSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Analysis failed")
    }



    fun getFormattingRules(userId: String): ResponseEntity<List<Rule>> {
        if (!assetService.assetExists("format", userId)) {
            return ResponseEntity.ok(RulesFactory().getDefaultFormattingRules())
        }
        val rulesString = assetService.getAsset(userId, "format")
        val mapper = jacksonObjectMapper()
        return ResponseEntity.ok(mapper.readValue(rulesString, object : TypeReference<List<Rule>>() {}))
    }
    fun modifyFormattingRule(authorization: String, rules: List<Rule>): List<Rule> {
        val userId = AuthorizationDecoder.decode(authorization)

        println("PARAAAAAAAAAAA")
        println(userId)
        println(rules)
        val mapper = jacksonObjectMapper()
        val rulesString = mapper.writeValueAsString(rules)
        if (assetService.assetExists("format", userId)) {
            assetService.updateAsset(userId, "format", rulesString)
        }
        assetService.addAsset(rulesString, "format", userId)
        return rules
    }





}
