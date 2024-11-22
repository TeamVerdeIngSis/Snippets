package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.RulesRequest
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionsService: PermissionsSerivce,
    private val assetService: AssetService,
) {
    private lateinit var parseService: ParseService

    fun setParseService(parseService: ParseService) {
        this.parseService = parseService
    }

    fun createSnippet(createSnippetRequest: CreateSnippetRequest, userId: String): Snippet {
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
        return snippet
    }

    fun helloParse(): ResponseEntity<String>{
        val response = parseService.hey()
        return ResponseEntity.ok(response)
    }

    fun helloPermissions(): ResponseEntity<String>{
        val response = permissionsService.hey()
        return ResponseEntity.ok(response)
    }

    fun shareSnippet(shareSnippetRequest: ShareSnippetRequest): String {
        return permissionsService.addPermission(shareSnippetRequest.userId, shareSnippetRequest.snippetId, "READ")
    }



    fun delete(id: String): String? {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }
        snippetRepository.delete(snippet)
        return assetService.deleteAsset(id, "snippets").body
    }

    fun updateSnippet(id: String, content: String): String?{
        return assetService.updateAsset(id, "snippets", content).body
    }

    fun getSnippet(id: String): Snippet {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found") }
        return snippet
    }

    fun getAllSnippetsByUser(userId: String): List<Snippet>? {
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        val snippets = ArrayList<Snippet>()
        if(snippetsID == null){
            return emptyList()
        }
        for (id in snippetsID){
            val snippet= getSnippet(id.snippetId)
            snippets.add(snippet)
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
    fun createLintingRules(lintingRulesRequest: RulesRequest): String {
        val rulesInString = lintingRulesRequest.rules.toString()
        assetService.addAsset(rulesInString, "linter",lintingRulesRequest.userId)
        return "Linting rules saved"
    }

    fun createFormatRules(formatRulesRequest: RulesRequest): String {
        val rulesInString = formatRulesRequest.rules.toString()
        assetService.addAsset(rulesInString, "formatter",formatRulesRequest.userId)
        return "Formatting rules saved"
    }
}