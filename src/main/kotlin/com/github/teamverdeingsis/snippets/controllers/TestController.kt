package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.TestDTO
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
@RequestMapping("/api/test")
class TestController (
    private val testService : TestServiceUi
    ){

    @GetMapping("/snippet/{id}")
    fun getTestsBySnippetId(@PathVariable id: String): ResponseEntity<List<TestDTO>>{
        val test = testService.getTestsBySnippetId(id)
        return ResponseEntity.ok(test.map { TestDTO(it) })
    }

    @PostMapping("/snippet/{id}")
    fun addTestToSnippet(
        @PathVariable id: String,
        @RequestBody testBody: Map<String, Any>
    ): ResponseEntity<TestDTO> {
        val testDTO = testService.addTestToSnippet(
            id,
            name = testBody["name"] as? String ?: "",
            input = testBody["input"] as? List<String> ?: listOf(),
            output = testBody["output"] as? List<String> ?: listOf()
        )
        return ResponseEntity.ok(testDTO)
    }

    @DeleteMapping("/{id}")
    fun deleteTestById(@PathVariable id: String): ResponseEntity<Void> {
        testService.deleteTestById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/run")
    fun runTest(@RequestHeader("Authorization") token: String, @PathVariable id: String): ResponseEntity<String> {
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

