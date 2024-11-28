package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FullSnippet
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import kotlin.jvm.optionals.getOrNull

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionsService: PermissionsSerivce,
    private val assetService: AssetService,
    private val parseService: ParseService,
    private val restTemplate: RestTemplate

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

    fun delete(id: String): String? {
        val snippet = snippetRepository.findById(id).getOrNull()
        if (snippet == null) {
            println("Nothing to delete")
            return null
        }
        snippetRepository.delete(snippet)
        println("Snippet with ID $id deleted")
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

    fun getAllSnippetsByUser(userId: String, username: String): List<SnippetWithAuthor>? {
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        val snippets = ArrayList<SnippetWithAuthor>()
        if (snippetsID == null) {
            return emptyList()
        }
        for (id in snippetsID) {
            val snippet = getSnippet(id.snippetId)
            snippets.add(SnippetWithAuthor(snippet ?: continue, username))
        }
        return snippets
    }

    data class SnippetWithAuthor(
        val snippet: Snippet,
        val author: String
    )

    fun validateSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.validateSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Validation failed")
    }

    fun executeSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.executeSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Execution failed")
    }

    fun analyzeSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.analyzeSnippet(createSnippetRequest)
        return response.body ?: throw RuntimeException("Analysis failed")
    }

    fun checkIfOwner(snippetId: String, userId: String, token: String): Boolean {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "userId" to userId) // Pasa el userId explícitamente
        val entity = HttpEntity(body, getJsonAuthorizedHeaders(token))

        val baseUrl = "http://localhost:8082/api/permissions"
        val checkOwnerEndpoint = "$baseUrl/check-owner"

        return try {
            val response = restTemplate.postForEntity(checkOwnerEndpoint, entity, String::class.java)
            response.body?.equals("User is the owner of the snippet", ignoreCase = true) == true
        } catch (e: Exception) {
            println("Error checking ownership: ${e.message}")
            false
        }
    }


    private fun getJsonAuthorizedHeaders(token: String): MultiValueMap<String, String> {
        return org.springframework.http.HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", token)
        }
    }

    fun shareSnippet(token: String, shareSnippetRequest: ShareSnippetRequest): ResponseEntity<FullSnippet> {
        val snippetId = shareSnippetRequest.snippetId
        val userId = shareSnippetRequest.userId // Extrae el userId directamente del request
        val fromEmail = shareSnippetRequest.fromEmail
        val toEmail = shareSnippetRequest.toEmail

        // Validar que no se comparte con uno mismo
        if (fromEmail == toEmail) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Share-Status", "You can't share a snippet with yourself")
                .body(null)
        }

        // Validar existencia del snippet
        val snippet = getSnippetWithContent(snippetId)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Share-Status", "Snippet not found")
                .body(null)

        // Validar si el usuario tiene permiso para compartir
        if (!checkIfOwner(snippetId, userId, token)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("Share-Status", "You are not the owner of the snippet")
                .body(null)
        }

        // Crear permiso para el destinatario
        permissionsService.addPermission(toEmail, snippetId, "READ")

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Share-Status", "Snippet shared with $toEmail")
            .body(snippet)
    }
}