package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.SnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class SnippetService(
    private val restTemplate: RestTemplate,
    private val snippetRepository: SnippetRepository
) {

    fun createSnippet(snippetRequest: SnippetRequest): Snippet {
        val assetId = uploadSnippetToAssetService(snippetRequest.content)

        val snippet = Snippet(
            name = snippetRequest.name,
            description = snippetRequest.description,
            language = snippetRequest.language,
            assetId = assetId
        )

        // Add logging to ensure the snippet is being saved and returned correctly
        println("Snippet Created: $snippet")

        return snippetRepository.save(snippet)
    }

    fun updateSnippet(id: String, snippetRequest: SnippetRequest): Snippet {
        val snippet = snippetRepository.findById(id).orElseThrow { RuntimeException("Snippet with ID $id not found")
        }

        val assetId = uploadSnippetToAssetService(snippetRequest.content)

        snippet.apply {
            name = snippetRequest.name
            description = snippetRequest.description
            language = snippetRequest.language
            this.assetId = assetId
        }

        return snippetRepository.save(snippet)
    }

    fun getSnippet(id: String): Snippet {
        return snippetRepository.findById(id)
            .orElseThrow {
                RuntimeException("Snippet with ID $id not found")
            }
    }


    private fun uploadSnippetToAssetService(content: String): String {
        val assetServiceUrl = "http://api:8080/upload"
        val headers = HttpHeaders().apply {
            contentType = MediaType.TEXT_PLAIN
        }

        val request = HttpEntity(content, headers)
        val response = restTemplate.postForEntity(assetServiceUrl, request, String::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return response.body ?: throw RuntimeException("AssetService returned null asset ID")
        } else {
            throw RuntimeException("Failed to upload snippet to AssetService")
        }
    }
}
