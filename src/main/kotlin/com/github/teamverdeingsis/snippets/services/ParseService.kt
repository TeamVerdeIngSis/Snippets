package com.github.teamverdeingsis.snippets.services


import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import org.springframework.web.client.getForObject

@Service
class ParseService(
    private val restTemplate: RestTemplate
) {

    private val parseServiceUrl = "http://localhost:8081/v1"


    fun hey(): String? {
        val url = "http://localhost:8081/api/parser/hola"
        println("llegue a la funcion")
        val response = restTemplate.getForObject(url, String::class.java)
        if (response == null){
            println("nada")
        }
        println("Response: $response")
        return response
    }
    // funcion para validar un snippet
    fun validateSnippet(createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(createSnippetRequest, headers)
        val url = "http://localhost:8081/parse/validate"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para ejecutar un snippet
    fun executeSnippet(createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(createSnippetRequest, headers)
        val url = "$parseServiceUrl/execute"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para formatear un snippet
    fun formatSnippet(createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(createSnippetRequest, headers)
        val url = "$parseServiceUrl/format"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }

    // funcion para analizar un snippet
    fun analyzeSnippet(createSnippetRequest: CreateSnippetRequest): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(createSnippetRequest, headers)
        val url = "$parseServiceUrl/analyze"

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }
}
