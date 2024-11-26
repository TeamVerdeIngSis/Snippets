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

    @GetMapping("/snippet/{id}")
    fun getTestsBySnippetId(@PathVariable id: String): ResponseEntity<List<TestDTO>> {
        val test = testService.getTestsBySnippetId(id)
        return ResponseEntity.ok(test.map { TestDTO(it) })
    }

    @PostMapping("/snippet/{id}")
    fun addTestToSnippet(
        @PathVariable id: String,
        @RequestBody testBody: Map<String, Any>
    ): ResponseEntity<TestDTO> {
        val name = testBody["name"] as? String ?: return ResponseEntity.badRequest().build()
        val input = testBody["input"] as? List<String> ?: emptyList()
        val output = testBody["output"] as? List<String> ?: emptyList()

        val testDTO = testService.addTestToSnippet(id, name, input, output)
        return ResponseEntity.ok(testDTO)
    }

    @DeleteMapping("/{id}")
    fun deleteTestById(@PathVariable id: String): ResponseEntity<Void> {
        testService.deleteTestById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/run")
    fun runTest(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: String
    ): ResponseEntity<String> {
        return testService.executeTest(token, id)
    }

    @PostMapping("/{id}/all")
    fun runAllTests(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: String
    ): ResponseEntity<Map<String, List<String>>> {
        val testResults = testService.executeAllSnippetTests(token, id)
        return ResponseEntity.ok(testResults)
    }
}
