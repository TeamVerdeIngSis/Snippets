package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
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
        println("zaaaap")
        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = "1",
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.languageName,
            languageVersion = createSnippetRequest.languageVersion,
            languageExtension = createSnippetRequest.languageExtension
        )
        println("HOLAHOLAHOLA")
        snippetRepository.save(snippet)
        println("CHAUCHAUCHAU")
        assetService.addAsset(createSnippetRequest.content, "snippets", snippet.id)
        return snippet
    }


    fun delete(id: String) {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }
        snippetRepository.delete(snippet)
        assetService.deleteAsset(id, "snippets")
    }

    fun updateSnippet(id: String, createSnippetRequest: CreateSnippetRequest) {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }
        assetService.updateAsset(id, "snippets", createSnippetRequest.content)
    }

    fun getSnippet(id: String): Snippet {
        return snippetRepository.findById(id)
            .orElseThrow {
                RuntimeException("Snippet with ID $id not found")
            }
    }

    fun getAllSnippetsByUser(userId: String): List<Snippet> {
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        val snippets = emptyList<Snippet>()
        for (id in snippetsID){
            val snippet= getSnippet(id.toString())
            snippets.plus(snippet)
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
}