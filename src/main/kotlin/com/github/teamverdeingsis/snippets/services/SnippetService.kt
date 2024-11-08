package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.SaveLintingRules
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class SnippetService(
    private val restTemplate: RestTemplate,
    private val snippetRepository: SnippetRepository,
    private val permissionsService: PermissionsSerivce,
    private val assetService: AssetService,
    private val parseService: ParseService
) {

    fun createSnippet(createSnippetRequest: CreateSnippetRequest): Snippet {
        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = "2",
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension
        )
        snippetRepository.save(snippet)
        assetService.addAsset(createSnippetRequest.content, "snippets", snippet.id)
        permissionsService.addPermission("2", snippet.id, "WRITE")
        return snippet
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

    fun getAllSnippetsByUser(userId: String): List<Snippet> {

        val snippetsIDs = permissionsService.getAllUserSnippets(userId)
        val snippets = emptyList<Snippet>().toMutableList()
        for (id in snippetsIDs){
            val snippet= getSnippet(id.snippetId)
            snippets += snippet
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
    fun createLintingRule(lintingRules: SaveLintingRules): String {
        val rulesInString = lintingRules.lintingRules.toString()
        assetService.addAsset(rulesInString, "linter",lintingRules.userId)
        return "Linting rules saved"
    }
}