package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.SnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*




@RestController
@RequestMapping("/api/snippets")
class SnippetController(
    private val snippetService: SnippetService
) {

    @PostMapping("/create")
    fun createSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<Snippet> {
        println("Creating snippet: $snippetRequest") // Debug log
        val snippet = snippetService.createSnippet(snippetRequest)
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
    ): ResponseEntity<Snippet> {
        val updatedSnippet = snippetService.updateSnippet(id, snippetRequest)
        return ResponseEntity.ok(updatedSnippet)
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

}
