// package com.github.teamverdeingsis.snippets.services
//
// import com.github.teamverdeingsis.snippets.factory.RulesFactory
// import com.github.teamverdeingsis.snippets.models.FormatSnippetRequest
// import com.github.teamverdeingsis.snippets.models.Rule
// import com.github.teamverdeingsis.snippets.producer.FormattingRuleProducer
//
// import org.junit.jupiter.api.Test
// import org.mockito.Mockito
// import org.mockito.kotlin.whenever
//
// import org.junit.jupiter.api.Assertions.*
// import org.mockito.InjectMocks
// import org.mockito.Mock
// import org.mockito.junit.jupiter.MockitoExtension
// import org.junit.jupiter.api.extension.ExtendWith
//
// @ExtendWith(MockitoExtension::class)
// class FormattingRulesServiceTest {
//
//    @Mock
//    private lateinit var assetService: AssetService
//
//    @Mock
//    private lateinit var snippetService: SnippetService
//
//    @Mock
//    private lateinit var producer: FormattingRuleProducer
//
//    @Mock
//    private lateinit var parseService: ParseService
//
//    @InjectMocks
//    private lateinit var formattingRulesService: FormattingRulesService
//
//
//    @Test
//    fun `test getFormattingRules returns default rules when no asset exists`() {
//        // Arrange
//        val userId = "user123"
//        val defaultRules = RulesFactory().getDefaultFormattingRules()
//
//        whenever(assetService.assetExists("format", userId)).thenReturn(false)
//
//        // Act
//        val result = formattingRulesService.getFormattingRules(userId)
//
//        // Assert
//        assertEquals(defaultRules, result)
//    }
//
//    @Test
//    fun `test getFormattingRules returns existing rules`() {
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
//        whenever(assetService.assetExists("format", userId)).thenReturn(true)
//        whenever(assetService.getAsset(userId, "format")).thenReturn(rulesString)
//
//        // Act
//        val result = formattingRulesService.getFormattingRules(userId)
//
//        // Assert
//        assertEquals(rules, result)
//    }
//
//    @Test
//    fun `test formatSnippet delegates to ParseService`() {
//        // Arrange
//        val snippetContent = FormatSnippetRequest("1","println('hello');")
//        val authorization = "Bearer valid-token"
//        val formattedSnippet = "formatted code"
//
//        whenever(parseService.formatSnippet(snippetContent, authorization)).thenReturn(formattedSnippet)
//
//        // Act
//        val result = formattingRulesService.formatSnippet(snippetContent, authorization)
//
//        // Assert
//        assertEquals(formattedSnippet, result)
//        Mockito.verify(parseService).formatSnippet(snippetContent, authorization)
//    }
// }
