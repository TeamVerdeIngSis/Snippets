package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.models.TestRequest
import com.github.teamverdeingsis.snippets.models.TestResponse
import com.github.teamverdeingsis.snippets.services.TestServiceUi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets")
class TestController(
    private val testService: TestServiceUi,
) {

    @GetMapping("/api/test/snippet/{snippetId}")
    fun getTestsBySnippetId(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String,
    ): ResponseEntity<List<TestDTO>> {
        val test = testService.getTestsBySnippetId(token, snippetId)
        return ResponseEntity.ok(test.map { TestDTO(it) })
    }

    @PostMapping("/api/test/snippet/{snippetId}")
    fun addTestToSnippet(
        @PathVariable snippetId: String,
        @RequestBody testRequest: TestRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<TestResponse> {
        println("checkpoint0, me llego este token: $token")
        println("checkpoint1, me llego esto del front: $testRequest")
        val testDTO = testService.addTestToSnippet(
            token,
            snippetId,
            testRequest.name,
            testRequest.input,
            testRequest.output
        )
        return ResponseEntity.ok(testDTO)
    }


    @DeleteMapping("/api/test/{testId}")
    fun deleteTestById(
        @RequestHeader("Authorization") token: String,
        @PathVariable testId: String,
    ): ResponseEntity<Void> {
        testService.deleteTestById(token, testId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/test/{testId}/run")
    fun runTest(
        @RequestHeader("Authorization") token: String,
        @PathVariable testId: String,
    ): ResponseEntity<String> {
        testService.getTestById(testId)
        return testService.executeTest(token, testId)
    }

    @PostMapping("/api/test/{snippetId}/all")
    fun runAllTests(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String,
    ): ResponseEntity<Map<String, List<String>>> {
        val testResults = testService.executeAllSnippetTests(token, snippetId)
        return ResponseEntity.ok(testResults)
    }
}
