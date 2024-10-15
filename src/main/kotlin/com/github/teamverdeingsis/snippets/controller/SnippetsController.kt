package com.github.teamverdeingsis.snippets.controller

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/snippets")
class SnippetsController {

    // Mock list to store snippets
    private val snippets = mutableListOf<String>()

    // GET: List all snippets (protected by read:snippets scope)
    @GetMapping
    fun getSnippets(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(snippets)
    }

    // POST: Add a new snippet (protected by write:snippets scope)
    @PostMapping
    fun addSnippet(@RequestBody snippet: String): ResponseEntity<String> {
        snippets.add(snippet)
        println("AAAAAA")
        println("Snippet added: $snippet")
        return ResponseEntity("Snippet added successfully", HttpStatus.CREATED)
    }
}
