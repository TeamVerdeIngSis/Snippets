package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FormatSnippetRequest
import com.github.teamverdeingsis.snippets.models.SnippetMessage
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder

@Service
class ParseService(
    private val restTemplate: RestTemplate
) {

    private val parseServiceUrl = "http://localhost:8081/v1"

    fun hey(): String? {
        val url = "http://localhost:8081/api/parser/hola"

        val response = restTemplate.getForObject(url, String::class.java)
        if (response == null) {
            println("nada")
        }
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

    fun lintSnippet(snippetID: String, authorization: String) {

        
        val userId = AuthorizationDecoder.decode(authorization)
        val url = "http://localhost:8081/api/parser/lint"
        val response = restTemplate.postForObject(url, SnippetMessage(snippetID, userId), String::class.java)


        // Define conformance según el resultado
        val conformance = if (response != "[]") {
            println("El snippet no cumple con las reglas de linting")
            Conformance.NOT_COMPLIANT
        } else {
            println("El snippet cumple con las reglas de linting")
            Conformance.COMPLIANT
        }

        val updateUrl = "http://localhost:8083/updateConformance"
        val requestBody = UpdateConformanceRequest(snippetID, conformance)
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", authorization)
        }
        val request = HttpEntity(requestBody, headers)
        restTemplate.postForObject(updateUrl, request, String::class.java)
    }

    fun formatSnippet(request: FormatSnippetRequest, authorization: String): String? {
        println("Starting formatSnippet function")

        val userId = AuthorizationDecoder.decode(authorization)
        println("Decoded userId: $userId")

        // Construye el cuerpo de la solicitud correctamente
        val requestBody = mapOf(
            "snippetId" to request.snippetId,
            "userId" to userId,
            "content" to request.content
        )
        println("Request body created: $requestBody")

        val url = "http://localhost:8081/api/parser/format"
        println("URL set: $url")

        return try {
            val response = restTemplate.postForObject(url, requestBody, String::class.java)
            println("Response received: $response")
            response
        } catch (e: Exception) {
            println("Error formatting snippet: ${e.message}")
            null
        }
    }


}
