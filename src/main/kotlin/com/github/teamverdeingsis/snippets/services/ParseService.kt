package com.github.teamverdeingsis.snippets.services

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.github.teamverdeingsis.snippets.models.SnippetRequest

@Service
class ParseService(
    private val restTemplate: RestTemplate
) {

    private val parseServiceUrl = "http://parse:8080/v1"

    // funcion para validar un snippet
    fun validateSnippet(snippetRequest: SnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(snippetRequest, headers)
        val url = "http://localhost:8080/parse/validate"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para ejecutar un snippet
    fun executeSnippet(snippetRequest: SnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(snippetRequest, headers)
        val url = "$parseServiceUrl/execute"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para formatear un snippet
    fun formatSnippet(snippetRequest: SnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(snippetRequest, headers)
        val url = "$parseServiceUrl/format"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para analizar un snippet
    fun analyzeSnippet(snippetRequest: SnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(snippetRequest, headers)
        val url = "$parseServiceUrl/analyze"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }
}
