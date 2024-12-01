package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.models.TestResponse
import com.github.teamverdeingsis.snippets.services.TestServiceUi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/snippets")
class TestController(
    private val testService: TestServiceUi
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
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String,
        @RequestBody testBody: Map<String, Any>
    ): ResponseEntity<TestResponse> {
        val name = testBody["name"] as? String ?: return ResponseEntity.badRequest().build()
        val input = testBody["input"] as? List<String> ?: emptyList()
        val output = testBody["output"] as? List<String> ?: emptyList()

        val testDTO = testService.addTestToSnippet(token, snippetId, name, input, output)
        return ResponseEntity.ok(testDTO)
    }

    @DeleteMapping("/api/test/{testId}")
    fun deleteTestById(
        @RequestHeader ("Authorization") token: String,
        @PathVariable testId: String): ResponseEntity<Void> {
        testService.deleteTestById(token, testId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/test/{testId}/run")
    fun runTest(
        @RequestHeader("Authorization") token: String,
        @PathVariable testId: String
    ): ResponseEntity<String> {
        val test = testService.getTestById(testId)
        return testService.executeTest(token, testId)
    }



    @PostMapping("/api/test/{snippetId}/all")
    fun runAllTests(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String
    ): ResponseEntity<Map<String, List<String>>> {
        val testResults = testService.executeAllSnippetTests(token, snippetId)
        return ResponseEntity.ok(testResults)
    }
}