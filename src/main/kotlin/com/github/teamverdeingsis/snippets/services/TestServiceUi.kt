package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Test
import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.repositories.TestRepo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TestServiceUi(
    private val testRepo: TestRepo,
    private val snippetRepository: SnippetRepository,
    private val parseService: ParseService,
) {

    fun getTestsBySnippetId(token: String, snippetId: String): List<Test> {
        return testRepo.findTestBySnippetId(snippetId)
    }

    fun getTestById(id: String): Test {
        return testRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Test not found") }
    }

    fun addTestToSnippet(token: String, snippetId: String, name: String, input: List<String>, output: List<String>): TestDTO {
        println("Buscando snippet con ID: $snippetId")
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { IllegalArgumentException("Snippet not found") }
        println("Snippet encontrado: $snippet")
        val test = Test(name = name, input = input, output = output, snippet = snippet)
        println("Guardando test: $test")
        testRepo.save(test)
        return TestDTO(test)
    }

    fun deleteTestById(token: String, id: String) {
        println("deleting test with id: $id")
        testRepo.deleteById(id)
        println("test deleted")
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
        val tests = getTestsBySnippetId(token, snippetId)
        return tests.associate { test ->
            val errors = parseService.test(token, test.snippet.id, test.input, test.output)
            test.name to errors
        }
    }
}