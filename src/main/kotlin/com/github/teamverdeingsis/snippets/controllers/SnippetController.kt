package com.github.teamverdeingsis.snippets.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FullSnippet
import com.github.teamverdeingsis.snippets.models.Rule
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.github.teamverdeingsis.snippets.models.UpdateSnippetRequest
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.github.teamverdeingsis.snippets.services.SnippetService
import com.nimbusds.jwt.JWTParser
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService) {

    @GetMapping("/hello/parse")
    fun helloParse(): ResponseEntity<String> {
        return snippetService.helloParse()
    }
    @GetMapping("/hello/permissions")
    fun helloPermissions(): ResponseEntity<String> {
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
        val snippet = snippetService.createSnippet(snippetRequest, authorization)
        return ResponseEntity.ok(snippet)
    }

    @DeleteMapping("/delete/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<String> {
        return ResponseEntity.ok(snippetService.delete(id))
    }

    @PutMapping("/update/{id}")
    fun updateSnippet(
        @RequestBody updateSnippetRequest: UpdateSnippetRequest,
        @PathVariable id: String,
    ): String? {
        val updatedSnippet = snippetService.updateSnippet(id, updateSnippetRequest.content)
        return ResponseEntity.ok(updatedSnippet).body
    }

    @GetMapping("/user/{id}")
    fun getSnippet(@PathVariable id: String): ResponseEntity<FullSnippet> {
        val snippet = snippetService.getSnippetWithContent(id)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping("/")
    fun getAllSnippetsByUser(
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<List<SnippetService.SnippetWithAuthor>?> {
        val token = authorization.removePrefix("Bearer ")
        val decodedJWT = JWTParser.parse(token)
        val userId = decodedJWT.jwtClaimsSet.subject
        val username = decodedJWT.jwtClaimsSet.getStringClaim("username")
        val snippets = snippetService.getAllSnippetsByUser(userId, username)
        return ResponseEntity.ok(snippets)
    }

    @PostMapping("/validate")
    fun validateSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.validateSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)

    }

    @PostMapping("/share")
    fun shareSnippet(@RequestBody shareSnippetRequest: ShareSnippetRequest,
                     @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<String> {
        val token = authorization.removePrefix("Bearer ")
        val decodedJWT = JWTParser.parse(token)
        val userId = decodedJWT.jwtClaimsSet.subject
        val result = snippetService.shareSnippet(shareSnippetRequest)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/execute")
    fun executeSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.executeSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }



    @PostMapping("/analyze")
    fun analyzeSnippet(@RequestBody createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val result = snippetService.analyzeSnippet(createSnippetRequest)
        return ResponseEntity.ok(result)
    }



}
