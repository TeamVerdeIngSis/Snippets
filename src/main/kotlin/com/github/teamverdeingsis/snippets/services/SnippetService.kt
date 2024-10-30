package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.Language
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

    fun createSnippet(createSnippetRequest: CreateSnippetRequest, userId: String): Snippet {
        val assetId = uploadSnippetToAssetService(createSnippetRequest.content)
        val user = permissionsService.getUsernameById(userId)
        val language = Language(
            name = createSnippetRequest.language,
            version = "1.0",
            extension = createSnippetRequest.extension
        )
        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = user,
            conformance = Conformance.PENDING,
            assetId = assetId,
            language = language
        )
        return snippetRepository.save(snippet)
    }

    fun delete(id: String) {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }
        snippetRepository.delete(snippet)
        assetService.deleteAsset(snippet.assetId, "snippets")
    }

    fun updateSnippet(id: String, createSnippetRequest: CreateSnippetRequest) {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }
        val asset = assetService.getAsset(snippet.assetId, "snippets")
        assetService.updateAsset(snippet.assetId, "snippets", createSnippetRequest.content)
    }

    fun getSnippet(id: String): Snippet {
        return snippetRepository.findById(id)
            .orElseThrow {
                RuntimeException("Snippet with ID $id not found")
            }
    }
    fun uploadSnippetToAssetService(content: String): String {
        val assetServiceUrl = "http://asset_service:8080/v1/asset/snippets/my-snippet.ps"
        val headers = HttpHeaders().apply {
            contentType = MediaType.TEXT_PLAIN
        }

        // Convertir el contenido a un tipo compatible con DataBuffer o Flow<DataBuffer>
        val request = HttpEntity(content, headers)

        val response = restTemplate.exchange(assetServiceUrl, HttpMethod.PUT, request, String::class.java)

        return if (response.statusCode == HttpStatus.OK || response.statusCode == HttpStatus.CREATED) {
            response.headers["Location"]?.firstOrNull() ?: throw RuntimeException("AssetService didn't return a location header")
        } else {
            throw RuntimeException("Failed to upload snippet to AssetService")
        }
    }

    fun getAllSnippetsByUser(userId: String): List<Snippet> {
        return permissionsService.getAllUserSnippets(userId)
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