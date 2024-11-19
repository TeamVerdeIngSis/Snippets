package com.github.teamverdeingsis.snippets.controllers


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.RulesRequest
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.UpdateSnippetRequest
import com.github.teamverdeingsis.snippets.services.SnippetService
import com.nimbusds.jwt.JWTParser
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService) {


    @GetMapping("/hello/parse")
    fun helloParse(): ResponseEntity<String> {
        println("AAAA")
        return snippetService.helloParse()
    }
    @GetMapping("/hello/permissions")
    fun helloPermissions(): ResponseEntity<String> {
        println("AAAA")
        return snippetService.helloPermissions()
    }

    @GetMapping("/hello/pablo")
    fun helloPablo(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, Pablo!")
    }
    @GetMapping("/hello/peter")
    fun helloPeter(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, Peter!")
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
        @RequestBody updateSnippetRequest: UpdateSnippetRequest, @PathVariable id: String,
    ): String? {
        val updatedSnippet = snippetService.updateSnippet(updateSnippetRequest.snippetId, updateSnippetRequest.content)
        return ResponseEntity.ok(updatedSnippet).body
    }

    @GetMapping("/")
    fun getAllSnippetsByUser(@RequestHeader("Authorization") authorization: String
    ): ResponseEntity<List<Snippet>?> {
        // Remover el prefijo "Bearer " del token
        val token = authorization.removePrefix("Bearer ")

        // Decodificar el token para obtener el userId
        val decodedJWT = JWTParser.parse(token)
        val userId = decodedJWT.jwtClaimsSet.subject
        val snippets = snippetService.getAllSnippetsByUser(userId)
        return ResponseEntity.ok(snippets)
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
    fun saveLintingRules(@RequestBody lintingRulesRequest: RulesRequest): ResponseEntity<String> {
        val result = snippetService.createLintingRules(lintingRulesRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/saveFormatRules")
    fun saveFormatRules(@RequestBody formattingRulesRequest: RulesRequest): ResponseEntity<String> {
        val result = snippetService.createFormatRules(formattingRulesRequest)
        return ResponseEntity.ok(result)
    }
}
