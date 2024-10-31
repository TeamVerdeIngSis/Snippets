package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.web.bind.annotation.*




@RestController
@RequestMapping("/api/snippets")
class SnippetController(
    private val snippetService: SnippetService
) {


    @GetMapping("/hello")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, World!")
    }
//    @PostMapping("/create")
//    fun createSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest, jwt: Jwt ): ResponseEntity<Snippet> {
//        println("Creating snippet: $createSnippetRequest") // Debug log
//        val userId = jwt.subject ?: throw RuntimeException("User ID not found in JWT")
//            val snippet = snippetService.createSnippet(createSnippetRequest, userId)
//        println("Snippet created: $snippet") // Debug log
//        return ResponseEntity.status(HttpStatus.CREATED).body(snippet)
//    }

@PostMapping("/create")
fun create(
    @RequestBody snippetRequest: CreateSnippetRequest,
    @RequestHeader("Authorization") token: String
): ResponseEntity<Snippet> {
    val snippet = snippetService.createSnippet(snippetRequest, token)
    return ResponseEntity.ok(snippet)
}

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        snippetService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: String,
        @RequestBody createSnippetRequest: CreateSnippetRequest
    ): ResponseEntity.BodyBuilder {
        val updatedSnippet = snippetService.updateSnippet(id, createSnippetRequest)
        return ResponseEntity.ok()
    }

    @GetMapping("/{id}")
    fun getSnippet(@PathVariable id: String): ResponseEntity<Snippet> {
        val snippet = snippetService.getSnippet(id)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping("/user/{userId}")
    fun getAllSnippetsByUser(@PathVariable userId: String): ResponseEntity<List<Snippet>> {
        val snippets = snippetService.getAllSnippetsByUser(userId)
        return ResponseEntity.ok(snippets)
    }

    @PostMapping("/validate")
    fun validateSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.validateSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/execute")
    fun executeSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.executeSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/format")
    fun formatSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.formatSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/analyze")
    fun analyzeSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.analyzeSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }

    fun getUserIdFromToken(token: String): String {
        val jwtDecoder: JwtDecoder = NimbusJwtDecoder.withIssuerLocation("https://dev-ppmfishyt4u8fel3.us.auth0.com/").build()
        val decodedJwt = jwtDecoder.decode(token)
        return decodedJwt.subject ?: throw RuntimeException("User ID not found in JWT")
    }
}
