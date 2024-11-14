package com.github.teamverdeingsis.snippets.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.UpdateSnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import com.nimbusds.jwt.JWTParser



@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService  ) {



    @GetMapping("/hello")
    fun hello(): ResponseEntity<String> {
        println("AAAAAAASFSAFJKSADKSAKDASK")
        return ResponseEntity.ok("Hello, World!")
    }

    @PostMapping("/create")
    fun create(
        @RequestBody snippetRequest: CreateSnippetRequest,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<Snippet> {
        // Remover el prefijo "Bearer " del token
        val token = authorization.removePrefix("Bearer ")

        // Decodificar el token para obtener el userId
        val decodedJWT = JWTParser.parse(token)
        val userId = decodedJWT.jwtClaimsSet.subject // Asumiendo que el userId está en "sub"

        // Llamar a snippetService.createSnippet con el userId
        val snippet = snippetService.createSnippet(snippetRequest, userId)

        return ResponseEntity.ok(snippet)
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<String> {
        return ResponseEntity.ok(snippetService.delete(id))
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @RequestBody updateSnippetRequest: UpdateSnippetRequest,
        @PathVariable id: String,
    ): ResponseEntity<String> {
        val updatedSnippet = snippetService.updateSnippet(updateSnippetRequest.snippetId, updateSnippetRequest.content)
        return ResponseEntity.ok(updatedSnippet)
    }

    @GetMapping("/{id}")
    fun getSnippet(@PathVariable id: String): ResponseEntity<String> {
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
}
