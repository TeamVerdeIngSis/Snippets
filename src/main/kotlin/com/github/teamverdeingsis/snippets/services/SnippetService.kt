package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FullSnippet
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.*
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
    private val restTemplate: RestTemplate,
    private val auth0Service: Auth0Service

) {

    fun createSnippet(createSnippetRequest: CreateSnippetRequest, authorization: String): CreateSnippetResponse {
        println("Creating snippet with request: $createSnippetRequest")  // Log de la solicitud

        val userId = AuthorizationDecoder.decode(authorization)
        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = userId,
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension
        )

        println("Snippet object created: $snippet")  // Log del snippet creado

        try {
            // Llamada al servicio de validación
            println("Passing snippet to parser for validation")
            val validationResult = parseService.validateSnippet(createSnippetRequest)
            println("Parse validation response: $validationResult")
            println(validationResult.length)

            // Si la respuesta es una lista vacía, el snippet es válido
            if (validationResult.length == 2) {
                // El snippet es válido, proceder con la creación en la base de datos
                println("AAAAAAAAAAAAA")
                snippetRepository.save(snippet)
                assetService.addAsset(createSnippetRequest.content, "snippets", snippet.id)
                permissionsService.addPermission(userId, snippet.id, "WRITE")
                println("ey")
                return CreateSnippetResponse(
                    message = "",
                    name = snippet.name,
                    content = createSnippetRequest.content,
                    language = snippet.languageName,
                    extension = snippet.languageExtension,
                    version = createSnippetRequest.version
                )
            } else {
                // Si la lista no está vacía, significa que hay errores de validación
                val errorMessages = validationResult
                val response = CreateSnippetResponse(
                    message = errorMessages,
                    name = "",
                    content = "",
                    language = "",
                    extension = "",
                    version = ""
                )
                println("Validation error: $errorMessages")  // Log de errores de validación
                return response  // Retornar el mensaje de error
            }
        } catch (e: Exception) {
            println("Validation error: ${e.message}")  // Log del error de validación

            return CreateSnippetResponse(
                message = "Unexpected error",
                name = "",
                content = "",
                language = "",
                extension = "",
                version = ""
            )
        }
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
        println("AAAAAAAAAAAAA llegue con $userId y $username")
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        val snippets = ArrayList<SnippetWithAuthor>()
        println("BBBBBBBBBBBBB")
        if (snippetsID == null) {
            return emptyList()
        }
        for (id in snippetsID) {
            val snippet = getSnippet(id.snippetId)
            val user = snippet?.userId?.let { auth0Service.getUserById(it) }
            if (user != null) {
                snippets.add(SnippetWithAuthor(snippet ?: continue, user.body?.nickname ?: "Unknown"))

            }
        }
        println("CCCCCCCCCCCCC")
        return snippets
    }

    data class SnippetWithAuthor(
        val snippet: Snippet,
        val author: String
    )

    fun validateSnippet(createSnippetRequest: CreateSnippetRequest): String {
        val response = parseService.validateSnippet(createSnippetRequest)
        return response ?: throw RuntimeException("Validation failed")
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
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "userId" to userId)
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
        val toUserId = shareSnippetRequest.userId // Usa directamente el userId
        val fromUserId = AuthorizationDecoder.decode(token)

        // Verifica que el snippet exista y el usuario tenga permiso
        val snippet = getSnippetWithContent(snippetId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        if (!checkIfOwner(snippetId, fromUserId, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
        }

        // Asigna permiso al destinatario
        permissionsService.addPermission(toUserId, snippetId, "READ")

        return ResponseEntity.ok(snippet)
    }
}