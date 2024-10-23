package com.github.teamverdeingsis.snippets.controller

import com.github.teamverdeingsis.snippets.dto.SnippetRequest
import com.github.teamverdeingsis.snippets.dto.SnippetResponse
import com.github.teamverdeingsis.snippets.service.SnippetService
import com.github.teamverdeingsis.snippets.service.ParseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/snippets")
class SnippetsController(private val snippetService: SnippetService) {

    // POST: Validate snippet
    @PostMapping("/validate")
    fun validateSnippet(){

    }

    // POST: Create a snippet using the editor
    @PostMapping("/editor")
    fun createSnippetInEditor(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<String> {
        // Validate the snippet using ParseService
        val isValid = parseService.validateSnippet(snippetRequest.content, snippetRequest.language, snippetRequest.version)
        if (!isValid) {
            return ResponseEntity.badRequest().body("El snippet no es válido")
        }

        // Create snippet in the service
        snippetService.createSnippetFromEditor(snippetRequest)
        return ResponseEntity.ok("Snippet creado exitosamente")
    }

    // POST: Upload a snippet using a file
    @PostMapping("/upload")
    fun uploadSnippet(@RequestParam("file") file: MultipartFile,
                      @RequestParam("name") name: String,
                      @RequestParam("description") description: String,
                      @RequestParam("language") language: String,
                      @RequestParam("version") version: String): ResponseEntity<String> {
        // Validate snippet using ParseService
        val isValid = parseService.validateSnippet(file, language, version)
        if (!isValid) {
            return ResponseEntity.badRequest().body("Snippet no válido según las reglas del parser")
        }

        // Save snippet via SnippetService
        snippetService.createSnippet(file, name, description, language, version)
        return ResponseEntity("Snippet uploaded successfully", HttpStatus.CREATED)
    }

    // PUT: Update a snippet by uploading a new file
    @PutMapping("/{id}/upload")
    fun updateSnippet(@PathVariable id: Long,
                      @RequestParam("file") file: MultipartFile,
                      @RequestParam("name", required = false) name: String?,
                      @RequestParam("description", required = false) description: String?,
                      @RequestParam("language") language: String,
                      @RequestParam("version") version: String): ResponseEntity<String> {
        val isValid = parseService.validateSnippet(file, language, version)
        if (!isValid) {
            return ResponseEntity.badRequest().body("Snippet no válido según las reglas del parser")
        }

        snippetService.updateSnippet(id, file, name, description, language, version)
        return ResponseEntity("Snippet updated successfully", HttpStatus.OK)
    }

    // GET: List all snippets
    @GetMapping
    fun getSnippets(): ResponseEntity<List<SnippetResponse>> {
        val snippets = snippetService.getAllSnippets()
        return ResponseEntity.ok(snippets)
    }

    // GET: Get a specific snippet by ID
    @GetMapping("/{id}")
    fun getSnippetById(@PathVariable id: Long): ResponseEntity<SnippetResponse> {
        val snippet = snippetService.findById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(snippet)
    }
}
