package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.SnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*




@RestController
@RequestMapping("/api/snippets")
class SnippetController(
    private val snippetService: SnippetService
) {

    @PostMapping("/create")
    fun createSnippet(@RequestBody snippetRequest: SnippetRequest, jwt: Jwt ): ResponseEntity<Snippet> {
        println("Creating snippet: $snippetRequest") // Debug log
        val userId = jwt.subject ?: throw RuntimeException("User ID not found in JWT")
        val snippet = snippetService.createSnippet(snippetRequest, userId)
        println("Snippet created: $snippet") // Debug log
        return ResponseEntity.status(HttpStatus.CREATED).body(snippet)
    }

    @PostMapping("/hello")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, World!")
    }


    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: String,
        @RequestBody snippetRequest: SnippetRequest
    ): ResponseEntity.BodyBuilder {
        val updatedSnippet = snippetService.updateSnippet(id, snippetRequest)
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
    fun validateSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<String> {
        val result = snippetService.validateSnippet(snippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/execute")
    fun executeSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<String> {
        val result = snippetService.executeSnippet(snippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/format")
    fun formatSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<String> {
        val result = snippetService.formatSnippet(snippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/analyze")
    fun analyzeSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<String> {
        val result = snippetService.analyzeSnippet(snippetRequest)
        return ResponseEntity.ok(result)
    }

}
