package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Test
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
    private val assetService: AssetService,
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
        outputs: List<String>,
    ): TestResponse {
        println("checkpoint3, llegue al service con esto: $name, $inputs, $outputs")
        val snippet = snippetRepository.findById(snippetId).orElseThrow {
            throw IllegalArgumentException("Snippet not found")
        }
        println("checkpoint4, busque el snippet y es: $snippet")
        val snippetContent = assetService.getAsset(snippetId, "snippets")
        println("checkpoint5, el contenido del snippet es: $snippetContent")
        val hasReadInput = snippetContent?.contains("readInput")
        println("checkpoint6, el snippet tiene readInput: $hasReadInput")
        if (!hasReadInput!! && inputs.isNotEmpty()) {
            println("checkpoint7, el snippet no tiene readInput y se le pasaron inputs, devolviendo response con msg")
            return TestResponse(
                id = "",
                name = "",
                input = listOf(),
                output = listOf(),
                message = "This snippet does not require inputs",
            )
        }

        if (hasReadInput && inputs.isEmpty()) {
            println("checkpoint8, el snippet tiene readInput y no se le pasaron inputs, devolviendo response con msg")
            return TestResponse(
                id = "",
                name = "",
                input = listOf(),
                output = listOf(),
                message = "This snippet requires inputs",
            )
        }

        println("checkpoint9, continuando con la creacion del test")

        val test = Test(
            name = name,
            input = inputs.toMutableList(),
            output = outputs.toMutableList(),
            snippet = snippet,
        )
        println("checkpoint10, cree el test: $test")
        testRepo.save(test)
        println("checkpoint11, guarde el test")
        println("checkpoint12, devolviendo response completa")
        return TestResponse(
            id = test.id,
            name = test.name,
            input = test.input,
            output = test.output,
            message = "Test added",
        )
    }

    fun deleteTestById(token: String, id: String) {
        testRepo.deleteById(id)
    }

    fun executeTest(token: String, testId: String): ResponseEntity<String> {
        val test = getTestById(testId)
        val snippet = snippetService.getSnippet(test.snippet.id)
        val results = parseService.test(token, test.snippet.id, test.input, test.output, snippet)

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
            val errors = parseService.test(token, test.snippet.id, test.input, test.output, snippet)
            test.name to errors
        }
    }
}
