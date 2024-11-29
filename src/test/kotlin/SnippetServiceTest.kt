import com.github.teamverdeingsis.snippets.models.*
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.github.teamverdeingsis.snippets.services.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*

@ExtendWith(MockitoExtension::class)
class SnippetServiceTest {
    private lateinit var snippetService: SnippetService
    private val snippetRepository: SnippetRepository = mock()
    private val permissionsService: PermissionsSerivce = mock()
    private val assetService: AssetService = mock()
    private val parseService: ParseService = mock()
    private val restTemplate: RestTemplate = mock()
    private val authorizationDecoder: AuthorizationDecoder = mock()

    @BeforeEach
    fun setUp() {
        snippetService = SnippetService(snippetRepository, permissionsService, assetService, parseService, restTemplate)
    }

    @Test
    fun `should create a new snippet if validation passes`() {
        val createSnippetRequest = CreateSnippetRequest(
            name = "Test Snippet",
            content = "This is a test",
            language = "kotlin",
            extension = "kt",
            version = "1.1"
        )
        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val userId = "user123"
        val snippet = Snippet(
            id = UUID.randomUUID().toString(),
            name = createSnippetRequest.name,
            userId = userId,
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension
        )

        whenever(authorizationDecoder.decode(authorization)).thenReturn(userId)
        whenever(parseService.validateSnippet(createSnippetRequest)).thenReturn("[]")
        whenever(snippetRepository.save(snippet)).thenReturn(snippet)

        val response = snippetService.createSnippet(createSnippetRequest, authorization)

        assertEquals("", response.message)
        assertEquals(createSnippetRequest.name, response.name)
        assertEquals(createSnippetRequest.content, response.content)
        assertEquals(createSnippetRequest.language, response.language)
        assertEquals(createSnippetRequest.extension, response.extension)
    }

    @Test
    fun `delete should return null if snippet is not found`() {
        val id = "123"
        `when`(snippetRepository.findById(id)).thenReturn(Optional.empty())

        val result = snippetService.delete(id)

        assertNull(result)
        verify(snippetRepository).findById(id)
        verifyNoInteractions(assetService)
    }

    @Test
    fun `updateSnippet should return updated snippet if validation passes`() {
        val id = "123"
        val content = "Updated content"
        val snippet = Snippet(id, "name", "user123", Conformance.PENDING, "PrintScript", "prs")
        val request = CreateSnippetRequest("name", content, "PrintScript", "prs", "1.1")

        `when`(snippetRepository.findById(id)).thenReturn(Optional.of(snippet))
        `when`(parseService.validateSnippet(request)).thenReturn("[]")
        `when`(assetService.updateAsset(id, "snippets", content)).thenReturn(ResponseEntity.ok("Content updated"))

        val response = snippetService.updateSnippet(id, content)

        assertEquals("Snippet updated", response.message)
        assertEquals(content, response.content)
        verify(snippetRepository).findById(id)
        verify(parseService).validateSnippet(request)
        verify(assetService).updateAsset(id, "snippets", content)
    }

    @Test
    fun `getSnippetWithContent should return snippet with content`() {
        val id = "123"
        val content = "Snippet content"
        val snippet = Snippet(id, "name", "user123", Conformance.PENDING, "Java", "java")
        val fullSnippet = FullSnippet(id, "name", "user123", Conformance.PENDING, "Java", "java", content)

        `when`(assetService.getAsset(id, "snippets")).thenReturn(content)
        `when`(snippetRepository.findById(id)).thenReturn(Optional.of(snippet))

        val result = snippetService.getSnippetWithContent(id)

        assertNotNull(result)
        assertEquals(fullSnippet, result)
        verify(snippetRepository).findById(id)
        verify(assetService).getAsset(id, "snippets")
    }
}