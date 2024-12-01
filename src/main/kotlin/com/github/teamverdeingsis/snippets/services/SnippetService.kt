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
    private val permissionsService: PermissionsService,
    private val assetService: AssetService,
    private val parseService: ParseService,
    private val restTemplate: RestTemplate,
    private val auth0Service: Auth0Service

) {

    fun createSnippet(createSnippetRequest: CreateSnippetRequest, authorization: String): CreateSnippetResponse {
        println("create checkpoint 2")
        println("Llegue a SnippetService.createSnippet() con $createSnippetRequest y $authorization")
        val userId = AuthorizationDecoder.decode(authorization)
        println("create checkpoint 3")
        println("Decodificado el userId: $userId")
        val snippet = Snippet(
            name = createSnippetRequest.name,
            userId = userId,
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension
        )

        try {
            println("create checkpoint 4, voy a validar el snippet")
            val validationResult = parseService.validateSnippet(createSnippetRequest)
            println("create checkpoint 5, validé el snippet")
            if (validationResult.length == 2) {
                println("create checkpoint 6, el snippet se puede parsear, voy a guardar el snippet")
                snippetRepository.save(snippet)
                println("create checkpoint 7, guardé el snippet")
                assetService.addAsset(createSnippetRequest.content, "snippets", snippet.id)
                println("create checkpoint 8, guardé el asset")
                permissionsService.addPermission(userId, snippet.id, "WRITE")
                println("create checkpoint 9, guardé los permisos")
                val response = CreateSnippetResponse(
                    message = "",
                    name = snippet.name,
                    content = createSnippetRequest.content,
                    language = snippet.languageName,
                    extension = snippet.languageExtension,
                    version = createSnippetRequest.version
                )
                println("create checkpoint 10, voy a retornar el snippet $response")
                return response
            } else {
                println("Hubo un error de validación, no se puede guardar el snippet")
                val errorMessages = validationResult
                val response = CreateSnippetResponse(
                    message = errorMessages,
                    name = "",
                    content = "",
                    language = "",
                    extension = "",
                    version = ""
                )
                println("Validation error: $errorMessages")
                return response
            }
        } catch (e: Exception) {
            println("Error creating snippet: ${e.message}")
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
        return assetService.deleteAsset(id, "snippets").body
    }

    fun updateSnippet(id: String, content: String): UpdateSnippetResponse {
        val snippet = snippetRepository.findById(id)
        val validationRequest = CreateSnippetRequest(
            name = snippet.get().name,
            content = content,
            language = snippet.get().languageName,
            extension = snippet.get().languageExtension,
            version = "1.1"
        )
        val validationResult = parseService.validateSnippet(validationRequest)
        if (validationResult.length == 2) {
            assetService.updateAsset(id, "snippets", content)
            return UpdateSnippetResponse(
                message = "Snippet updated",
                name = snippet.get().name,
                content = content,
                language = snippet.get().languageName,
                extension = snippet.get().languageExtension,
                version = "1.1"
            )
        } else {
            val errorMessages = validationResult
            return UpdateSnippetResponse(
                message = errorMessages,
                name = "",
                content = "",
                language = "",
                extension = "",
                version = ""
            )
        }
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
        println("getAllSnippetsByUser checkpoint 4, llegue a getAllSnippetsByUser con $userId y $username")
        val snippetsID = permissionsService.getAllUserSnippets(userId)
        println("getAllSnippetsByUser checkpoint 5, obtuve los snippets")
        val snippets = ArrayList<SnippetWithAuthor>()
        if (snippetsID == emptyList<Permission>()) {
            println("no hay snippets")
            return emptyList()
        }
        println("getAllSnippetsByUser checkpoint 6, voy a iterar sobre los snippets")
        for (id in snippetsID) {
            val snippet = getSnippet(id.snippetId)
            println("iterando sobre los snippets")
            println(snippet?.userId)
            val user = snippet?.userId?.let { auth0Service.getUserById(it) }
            if (user != null) {
                snippets.add(SnippetWithAuthor(snippet ?: continue, user.body?.nickname ?: "Unknown"))

            }
        }
        println("getAllSnippetsByUser checkpoint 7, voy a retornar los snippets")
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

        val baseUrl = "http://permissions-service-infra:8080/api/permissions"
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