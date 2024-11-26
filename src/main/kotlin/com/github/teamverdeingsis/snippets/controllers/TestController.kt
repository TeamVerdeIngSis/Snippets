package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.services.TestServiceUi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestController(
    private val testService: TestServiceUi
) {

    @GetMapping("/snippet/{snippetId}")
    fun getTestsBySnippetId(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String,
    ): ResponseEntity<List<TestDTO>> {
        val test = testService.getTestsBySnippetId(token, snippetId)
        return ResponseEntity.ok(test.map { TestDTO(it) })
    }

    @PostMapping("/snippet/{snippetId}")
    fun addTestToSnippet(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String,
        @RequestBody testBody: Map<String, Any>
    ): ResponseEntity<TestDTO> {
        println("Adding test to snippet with ID: $snippetId")
        val name = testBody["name"] as? String ?: return ResponseEntity.badRequest().build()
        val input = testBody["input"] as? List<String> ?: emptyList()
        val output = testBody["output"] as? List<String> ?: emptyList()

        val testDTO = testService.addTestToSnippet(token, snippetId, name, input, output)
        return ResponseEntity.ok(testDTO)
    }

    @DeleteMapping("/{testId}")
    fun deleteTestById(
        @RequestHeader ("Authorization") token: String,
        @PathVariable testId: String): ResponseEntity<Void> {
        testService.deleteTestById(token, testId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{testId}/run")
    fun runTest(
        @RequestHeader("Authorization") token: String,
        @PathVariable testId: String
    ): ResponseEntity<String> {
        println("Received request to run test with ID: $testId")
        println("Authorization header: $token")
        return testService.executeTest(token, testId)
    }


    @PostMapping("/{snippetId}/all")
    fun runAllTests(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: String
    ): ResponseEntity<Map<String, List<String>>> {
        val testResults = testService.executeAllSnippetTests(token, snippetId)
        return ResponseEntity.ok(testResults)
    }
}