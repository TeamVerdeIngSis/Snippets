package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FormatSnippetRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@ExtendWith(SpringExtension::class)
class ParseServiceTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    @InjectMocks
    lateinit var parseService: ParseService

    // Test para el método 'hey'
    @Test
    fun `hey should return response from parse service`() {
        val expectedResponse = "Hello from parse service"
        val url = "https://teamverde.westus2.cloudapp.azure.com/api/parser/hola"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.getForObject(url, String::class.java)).thenReturn(expectedResponse)

        val result = parseService.hey()

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        verify(restTemplate).getForObject(url, String::class.java)
    }

    @Test
    fun `validateSnippet should return validation response`() {
        val createSnippetRequest = CreateSnippetRequest(name="name", content = "sample code", language = "PrintScript", extension = "prs", version = "1.1")
        val expectedResponse = "Valid snippet"
        val url = "http://parse-service-infra:8080/api/parser/validate"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.postForObject(eq(url), any(), eq(String::class.java)))
            .thenReturn(expectedResponse)

        val result = parseService.validateSnippet(createSnippetRequest)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        verify(restTemplate).postForObject(eq(url), any(), eq(String::class.java))
    }

    // Test para el método 'executeSnippet'
    @Test
    fun `executeSnippet should return response entity with content`() {
        val createSnippetRequest = CreateSnippetRequest(name="name", content = "sample code", language = "PrintScript", extension = "prs", version = "1.1")
        val expectedResponse = "Execution result"
        val url = "http://parse-service-infra:8080/v1/execute"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok(expectedResponse))

        val result = parseService.executeSnippet(createSnippetRequest)

        assertNotNull(result)
        assertEquals(expectedResponse, result.body)
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java))
    }

    // Test para el método 'formatSnippet'
    @Test
    fun `formatSnippet should return formatted snippet response`() {
        val createSnippetRequest = CreateSnippetRequest(name="name", content = "sample code", language = "PrintScript", extension = "prs", version = "1.1")
        val expectedResponse = "Formatted snippet"
        val url = "http://parse-service-infra:8080/v1/format"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok(expectedResponse))

        val result = parseService.formatSnippet(createSnippetRequest)

        assertNotNull(result)
        assertEquals(expectedResponse, result.body)
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java))
    }

    // Test para el método 'analyzeSnippet'
    @Test
    fun `analyzeSnippet should return analysis response`() {
        val createSnippetRequest = CreateSnippetRequest(name="name", content = "sample code", language = "PrintScript", extension = "prs", version = "1.1")
        val expectedResponse = "Analysis result"
        val url = "http://parse-service-infra:8080/v1/analyze"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok(expectedResponse))

        val result = parseService.analyzeSnippet(createSnippetRequest)

        assertNotNull(result)
        assertEquals(expectedResponse, result.body)
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String::class.java))
    }


    // Test para el método 'test' (con DTO y parámetros)
    @Test
    fun `test should return test results from parser`() {
        val token = "Bearer someToken"
        val snippetId = "123"
        val inputs = listOf("input1", "input2")
        val outputs = listOf("output1", "output2")
        val snippet = null // This is not used in the current implementation
        val expectedResponse = listOf("test result 1", "test result 2")
        val url = "http://parse-service-infra:8080/api/parser/test"

        // Mock de la respuesta del RestTemplate
        val responseEntity = ResponseEntity.ok(expectedResponse)
        whenever(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(), eq(object : ParameterizedTypeReference<List<String>>() {})))
            .thenReturn(responseEntity)

        val result = parseService.test(token, snippetId, inputs, outputs, snippet)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(object : ParameterizedTypeReference<List<String>>() {}))
    }

    // Test para el método 'lintSnippet'
    @Test
    fun `lintSnippet should update conformance based on linting result`() {
        val snippetId = "123"
        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val lintResponse = "[]"
        val expectedConformance = Conformance.COMPLIANT

        val urlLint = "http://parse-service-infra:8080/api/parser/lint"
        val urlUpdateConformance = "http://snippets-service-infra:8080/updateConformance"

        // Mock de la respuesta del RestTemplate para linting
        whenever(restTemplate.postForObject(eq(urlLint), any(), eq(String::class.java)))
            .thenReturn(lintResponse)

        // Mock de la llamada para actualizar la conformidad
        whenever(restTemplate.postForObject(eq(urlUpdateConformance), any(), eq(String::class.java)))
            .thenReturn("Conformance updated")

        // Llamada al método que estás probando
        parseService.lintSnippet(snippetId, authorization)

        // Verificar las llamadas y el flujo
        verify(restTemplate).postForObject(eq(urlLint), any(), eq(String::class.java))
        verify(restTemplate).postForObject(eq(urlUpdateConformance), any(), eq(String::class.java))
    }

    @Test
    fun `formatSnippet should return formatted snippet response with token`() {
        // Arrange
        val formatSnippetRequest = FormatSnippetRequest(snippetId = "123", content = "sample code")
        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val expectedResponse = "Formatted snippet"
        val url = "http://parse-service-infra:8080/api/parser/format"

        // Mock de la respuesta del RestTemplate
        whenever(restTemplate.postForObject(eq(url), any(), eq(String::class.java)))
            .thenReturn(expectedResponse)

        // Act
        val result = parseService.formatSnippet(formatSnippetRequest, authorization)

        // Assert
        assertNotNull(result)
        assertEquals(expectedResponse, result)
        verify(restTemplate).postForObject(eq(url), any(), eq(String::class.java))
    }



}
