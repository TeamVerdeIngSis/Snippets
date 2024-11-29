//package com.github.teamverdeingsis.snippets.services
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.github.teamverdeingsis.snippets.factory.RulesFactory
//import com.github.teamverdeingsis.snippets.models.Conformance
//import com.github.teamverdeingsis.snippets.models.Rule
//import com.github.teamverdeingsis.snippets.models.Snippet
//import com.github.teamverdeingsis.snippets.producer.LinterRuleProducer
//import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
//import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.whenever
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import org.junit.jupiter.api.Assertions.*
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.kotlin.doReturn
//import org.springframework.http.ResponseEntity
//
//@ExtendWith(MockitoExtension::class)
//class LintingRulesServiceTest {
//
//    @Mock
//    private lateinit var assetService: AssetService
//
//    @Mock
//    private lateinit var snippetRepository: SnippetRepository
//
//    @Mock
//    private lateinit var snippetService: SnippetService
//
//    @Mock
//    private lateinit var producer: LinterRuleProducer
//
//    @InjectMocks
//    private lateinit var lintingRulesService: LintingRulesService
//
//
//    @Test
//    fun `test getLintingRules returns default rules when no asset exists`() {
//        // Arrange
//        val userId = "user123"
//        val defaultRules = RulesFactory().getDefaultLintingRules()
//
//        whenever(assetService.assetExists("linting", userId)).thenReturn(false)
//
//        // Act
//        val result = lintingRulesService.getLintingRules(userId)
//
//        // Assert
//        assertEquals(defaultRules, result)
//    }
//
//    @Test
//    fun `test getLintingRules returns existing rules`() {
//        // Arrange
//        val userId = "user123"
//        val rulesString = """
//            [
//                {"id": "1", "name": "rule1", "isActive": false},
//                {"id": "2", "name": "rule2", "isActive": true}
//            ]
//        """
//        val rules = listOf(
//            Rule("1", "rule1", false),
//            Rule("2", "rule2", true)
//        )
//
//        whenever(assetService.assetExists("linting", userId)).thenReturn(true)
//        whenever(assetService.getAsset(userId, "linting")).thenReturn(rulesString)
//
//        // Act
//        val result = lintingRulesService.getLintingRules(userId)
//
//        // Assert
//        assertEquals(rules, result)
//    }
//
//    @Test
//    fun `test updateConformance updates snippet conformance`() {
//        // Arrange
//        val snippetId = "snippet123"
//        val mocked_conformance = Conformance.PENDING
//        val snippet = Snippet(
//            id = snippetId,
//            name = "snippet",
//            userId = "user123",
//            conformance = Conformance.NOT_COMPLIANT,
//            languageName = "kotlin",
//            languageExtension = "kt",
//        )
//
//        whenever(snippetRepository.findById(snippetId)).thenReturn(java.util.Optional.of(snippet))
//
//        // Act
//        lintingRulesService.updateConformance(snippetId, mocked_conformance)
//
//        // Assert
//        verify(snippetRepository).save(snippet)
//        assertEquals(mocked_conformance, snippet.conformance)
//    }
//}