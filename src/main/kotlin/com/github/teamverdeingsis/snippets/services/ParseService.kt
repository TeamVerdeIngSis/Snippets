package com.github.teamverdeingsis.snippets.services

import TestParseDTO
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
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.models.SnippetMessage
import com.github.teamverdeingsis.snippets.models.UpdateConformanceRequest
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.util.MultiValueMap

@Service
class ParseService(
    private val restTemplate: RestTemplate
) {

    private val parseServiceUrl = "http://parse-service-infra:8080/v1"

    fun hey(): String? {
        val url = "https://teamverde.westus2.cloudapp.azure.com/api/parser/hola"

        val response = restTemplate.getForObject(url, String::class.java)
        if (response == null) {
            println("nada")
        }
        return response
    }

    // funcion para validar un snippet
    fun validateSnippet(createSnippetRequest: CreateSnippetRequest): String {
        println("Sending snippet to parse service for validation: ${createSnippetRequest.content}")  // Log del snippet a validar
        val response = restTemplate.postForObject(
            "http://parse-service-infra:8080/api/parser/validate",
            mapOf("code" to createSnippetRequest.content, "version" to createSnippetRequest.version),
            String::class.java
        )
        println("Parse validation response desde parseService: $response")  // Log de la respuesta del parser
        println(response)
        return response ?: "Error: No response from parser"
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
        val url = "http://parse-service-infra:8080/api/parser/lint"
        val response = restTemplate.postForObject(url, SnippetMessage(snippetID, userId), String::class.java)


        // Define conformance según el resultado
        val conformance = if (response != "[]") {
            println("El snippet no cumple con las reglas de linting")
            Conformance.NOT_COMPLIANT
        } else {
            println("El snippet cumple con las reglas de linting")
            Conformance.COMPLIANT
        }

        val updateUrl = "http://snippets-service-infra:8080/updateConformance"
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

        val url = "http://parse-service-infra:8080/api/parser/format"
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


    fun test(
        token: String,
        snippetId: String,
        inputs: List<String>,
        outputs: List<String>,
        snippet: Snippet?
    ): List<String> {
        val testDTO = TestParseDTO(
            version = "1.1",
            snippetId = snippetId,
            inputs = inputs,
            outputs = outputs
        )
        println("Sending to parser: $testDTO")
        val headers = getJsonAuthorizedHeaders(token)
        val entity = HttpEntity(testDTO, headers)
        val response = restTemplate.exchange(
            "http://parse-service-infra:8080/api/parser/test",
            HttpMethod.POST,
            entity,
            object : ParameterizedTypeReference<List<String>>() {}
        )
        println("Response from parser: ${response.body}")
        return response.body ?: emptyList()
    }

    private fun getJsonAuthorizedHeaders(token: String): MultiValueMap<String, String> {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", token)
        }
    }
}