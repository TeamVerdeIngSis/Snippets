package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance

import TestParseDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.SnippetMessage
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.nimbusds.jwt.JWTParser
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import org.springframework.core.ParameterizedTypeReference
import org.springframework.util.MultiValueMap
import org.springframework.web.client.getForObject

@Service
class ParseService(
    private val restTemplate: RestTemplate
) {
    private val parseServiceUrl = "http://localhost:8089/v1"
    private lateinit var snippetService: SnippetService

    fun setSnippetService(snippetService: SnippetService) {
        this.snippetService = snippetService
    }


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

    fun lintSnippet(snippetID: String, authorization: String) {
        val token = authorization.removePrefix("Bearer ")
        val decodedJWT = JWTParser.parse(token)
        val userId = decodedJWT.jwtClaimsSet.subject // Asumiendo que el userId está en "sub"
        val url = "http://localhost:8089/api/parser/lint"
        val response = restTemplate.postForObject(url, SnippetMessage(snippetID, userId), String::class.java)


        // Define conformance según el resultado
        val conformance = if (response != "[]") {
            println("El snippet no cumple con las reglas de linting")
            Conformance.NOT_COMPLIANT
        } else {
            println("El snippet cumple con las reglas de linting")
            Conformance.COMPLIANT
        }

        val updateUrl = "http://localhost:8083/snippets/updateConformance"
        val requestBody = UpdateConformanceRequest(snippetID, conformance)
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", authorization)
        }
        val request = HttpEntity(requestBody, headers)
        restTemplate.postForObject(updateUrl, request, String::class.java)
    }

    fun test(
        token: String,
        snippetId: String,
        inputs: List<String>,
        outputs: List<String>
    ): List<String> {
        val snippet = snippetService.getSnippet(snippetId)
        val testDTO = TestParseDTO(
            snippetId = snippet.id.toLong(),
            inputs = inputs,
            outputs = outputs
        )

        val headers = getJsonAuthorizedHeaders(token)
        val entity = HttpEntity(testDTO, headers)

        val response = restTemplate.exchange(
            "http://parse:8089/api/parser/test",
            HttpMethod.POST,
            entity,
            object : ParameterizedTypeReference<List<String>>() {}
        )

        return response.body ?: emptyList()
    }
    private fun getJsonHeaders(): MultiValueMap<String, String>? {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }
    private fun getJsonAuthorizedHeaders(token: String): MultiValueMap<String, String> {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", token)
        }
    }
}
