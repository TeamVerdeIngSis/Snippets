package com.github.teamverdeingsis.snippets.controllers


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.SaveLintingRules
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.UpdateSnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import org.springframework.web.bind.annotation.CrossOrigin

@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService) {


    @GetMapping("/hello")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, World!")
    }

    @CrossOrigin(origins = arrayOf("http://localhost:5173"))
    @PostMapping("/create")
    fun create(
        @RequestBody snippetRequest: CreateSnippetRequest
    ): ResponseEntity<Snippet> {
        val snippet = snippetService.createSnippet(snippetRequest)
        return ResponseEntity.ok(snippet)
    }

    @PostMapping("/create1")
    fun createSnippet(
        @RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<Snippet> {
        val snippet = snippetService.createSnippet(createSnippetRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(snippet)
    }


    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<String> {
        return ResponseEntity.ok(snippetService.delete(id))
    }



    @PutMapping("/{id}")
    fun updateSnippet(
        @RequestBody updateSnippetRequest: UpdateSnippetRequest, @PathVariable id: String,
    ): String? {
        val updatedSnippet = snippetService.updateSnippet(updateSnippetRequest.snippetId, updateSnippetRequest.content)
        return ResponseEntity.ok(updatedSnippet).body
    }

    @GetMapping("/{id}")
    fun getSnippet(@PathVariable id: String): ResponseEntity<Snippet> {
        val snippet = snippetService.getSnippet(id)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping("/user/{userId}")
    fun getAllSnippetsByUser(@PathVariable userId: String): ResponseEntity<String> {
        val snippets = snippetService.getAllSnippetsByUser(userId)
        return ResponseEntity.ok(snippets.toString())
    }

    @PostMapping("/validate")
    fun validateSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.validateSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }


    @PostMapping("/share")
    fun shareSnippet(@RequestBody shareSnippetRequest: ShareSnippetRequest): ResponseEntity<String> {
        val result = snippetService.shareSnippet(shareSnippetRequest)
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

    @PostMapping("/saveLintingRules")
    fun saveLintingRules(@RequestBody lintingRules: SaveLintingRules): ResponseEntity<String> {
        val result = snippetService.createLintingRule(lintingRules)
        return ResponseEntity.ok(result)
    }
}
