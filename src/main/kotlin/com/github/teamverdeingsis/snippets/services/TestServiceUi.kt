package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Test
import com.github.teamverdeingsis.snippets.models.TestDTO
import com.github.teamverdeingsis.snippets.models.TestResponse
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.repositories.TestRepo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TestServiceUi(
    private val testRepo: TestRepo,
    private val snippetRepository: SnippetRepository,
    private val parseService: ParseService,
    private val snippetService: SnippetService,
    private val assetService: AssetService
) {

    fun getTestsBySnippetId(token: String, snippetId: String): List<Test> {
        return testRepo.findTestBySnippetId(snippetId)
    }

    fun getTestById(id: String): Test {
        return testRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Test not found") }
    }

    fun addTestToSnippet(
        token: String,
        snippetId: String,
        name: String,
        inputs: List<String>,
        outputs: List<String>
    ): TestResponse {

        val snippet = snippetRepository.findById(snippetId).orElseThrow {
            throw IllegalArgumentException("Snippet not found")
        }

        val snippetContent = assetService.getAsset( snippetId,"snippets")
        val hasReadInput = snippetContent?.contains("readInput")

        if (!hasReadInput!! && inputs.isNotEmpty()) {
            return TestResponse(
                id = "",
                name = "",
                input = listOf(),
                output = listOf(),
                message = "This snippet does not require inputs"
            )
        }

        if (hasReadInput && inputs.isEmpty()) {
            return TestResponse(
                id = "",
                name = "",
                input = listOf(),
                output = listOf(),
                message = "This snippet requires inputs"
            )
        }

        val test = Test(
            name = name,
            input = inputs.toMutableList(),
            output = outputs.toMutableList(),
            snippet = snippet
        )

        testRepo.save(test)
        return TestResponse(
            id = test.id,
            name = test.name,
            input = test.input,
            output = test.output,
            message = "Test added"
        )
    }

    fun deleteTestById(token: String, id: String) {
        testRepo.deleteById(id)
    }

    fun executeTest(token: String, testId: String): ResponseEntity<String> {
        val test = getTestById(testId)
        val snippet = snippetService.getSnippet(test.snippet.id)
        val results = parseService.test(token, test.snippet.id, test.input, test.output,snippet)

        return if (results.isEmpty()) {
            ResponseEntity.ok("test passed")
        } else {
            ResponseEntity.ok("test failed")
        }
    }

    fun executeAllSnippetTests(token: String, snippetId: String): Map<String, List<String>> {
        val tests = getTestsBySnippetId(token, snippetId)
        val snippet = snippetService.getSnippet(snippetId)
        return tests.associate { test ->
            val errors = parseService.test(token, test.snippet.id, test.input, test.output,snippet)
            test.name to errors
        }
    }
}