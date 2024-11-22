package com.github.teamverdeingsis.snippets.services;

import com.github.teamverdeingsis.snippets.models.Test
import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.repositories.TestRepo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service;

@Service
class TestServiceUi(
    private val testRepo: TestRepo,
    private val snippetRepository: SnippetRepository,
    private val parseService: ParseService,
) {

    fun getTestsBySnippetId(snippetId: String): List<Test> {
        return testRepo.findTestBySnippetId(snippetId)
    }

    fun getTestById(id: String): Test {
        return testRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Test not found") }
    }

    fun addTestToSnippet(snippetId: String, name: String, input: List<String>, output: List<String>): TestDTO {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { IllegalArgumentException("Snippet not found") }
        val test = Test(name = name, input = input, output = output, snippet = snippet)
        testRepo.save(test)
        return TestDTO(test)
    }

    fun deleteTestById(id: String) {
        testRepo.deleteById(id)
    }

    fun executeTest(token: String, testId: String): ResponseEntity<String> {
        val test = getTestById(testId)
        val results = parseService.test(token, test.snippet.id, test.input, test.output)

        return if (results.isEmpty()) {
            ResponseEntity.ok("success")
        } else {
            ResponseEntity.ok("fail")
        }
    }

    fun executeAllSnippetTests(token: String, snippetId: String): Map<String, List<String>> {
        val tests = getTestsBySnippetId(snippetId)
        return tests.associate { test ->
            val errors = parseService.test(token, test.snippet.id, test.input, test.output)
            test.name to errors
        }
    }
}
