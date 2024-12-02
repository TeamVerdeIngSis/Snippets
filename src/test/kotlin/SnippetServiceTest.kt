import com.github.teamverdeingsis.snippets.models.Conformance
import com.github.teamverdeingsis.snippets.models.CreateSnippetRequest
import com.github.teamverdeingsis.snippets.models.FullSnippet
import com.github.teamverdeingsis.snippets.models.ShareSnippetRequest
import com.github.teamverdeingsis.snippets.models.Snippet
import com.github.teamverdeingsis.snippets.repositories.SnippetRepository
import com.github.teamverdeingsis.snippets.security.AuthorizationDecoder
import com.github.teamverdeingsis.snippets.services.AssetService
import com.github.teamverdeingsis.snippets.services.Auth0Service
import com.github.teamverdeingsis.snippets.services.ParseService
import com.github.teamverdeingsis.snippets.services.PermissionsService
import com.github.teamverdeingsis.snippets.services.SnippetService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*

@ExtendWith(MockitoExtension::class)
class SnippetServiceTest {
    private lateinit var snippetService: SnippetService
    private val snippetRepository: SnippetRepository = mock()
    private val permissionsService: PermissionsService = mock()
    private val assetService: AssetService = mock()
    private val parseService: ParseService = mock()
    private val restTemplate: RestTemplate = mock()
    private val authorizationDecoder: AuthorizationDecoder = mock()
    private val auth0Service: Auth0Service = mock()

    @BeforeEach
    fun setUp() {
        snippetService = SnippetService(snippetRepository, permissionsService, assetService, parseService, restTemplate, auth0Service)
    }

    @Test
    fun `should create a new snippet if validation passes`() {
        val createSnippetRequest = CreateSnippetRequest(
            name = "Test Snippet",
            content = "This is a test",
            language = "kotlin",
            extension = "kt",
            version = "1.1",
        )
        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val userId = "user123"
        val snippet = Snippet(
            id = UUID.randomUUID().toString(),
            name = createSnippetRequest.name,
            userId = userId,
            conformance = Conformance.PENDING,
            languageName = createSnippetRequest.language,
            languageExtension = createSnippetRequest.extension,
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

    @Test
    fun `checkIfOwner should return true if user is the owner`() {
        val snippetId = "123"
        val userId = "user123"
        val token = "Bearer some_valid_token"
        val responseBody = "User is the owner of the snippet"
        val response = ResponseEntity(responseBody, HttpStatus.OK)

        whenever(restTemplate.postForEntity(anyString(), any(), eq(String::class.java)))
            .thenReturn(response)

        val result = snippetService.checkIfOwner(snippetId, userId, token)

        assertTrue(result)
        verify(restTemplate).postForEntity(anyString(), any(), eq(String::class.java)) // Verificamos la llamada
    }

    @Test
    fun `executeSnippet should return the execution result`() {
        val request = CreateSnippetRequest(name = "Test Snippet", content = "println('Hello World')", language = "kotlin", extension = "kt", version = "1.0")
        val executionResult = "Execution result: Hello World"

        whenever(parseService.executeSnippet(request)).thenReturn(ResponseEntity.ok(executionResult))

        val result = snippetService.executeSnippet(request)

        assertEquals(executionResult, result)
    }

    @Test
    fun `validateSnippet should return validation errors if invalid`() {
        val request = CreateSnippetRequest(name = "Test Snippet", content = "println('Hello World')", language = "kotlin", extension = "kt", version = "1.0")
        val validationResult = "Error: Invalid syntax"

        whenever(parseService.validateSnippet(request)).thenReturn(validationResult)

        val result = snippetService.validateSnippet(request)

        assertEquals(validationResult, result)
    }

    @Test
    fun `validateSnippet should return empty string if valid`() {
        val request = CreateSnippetRequest(name = "Test Snippet", content = "println('Hello World')", language = "kotlin", extension = "kt", version = "1.0")
        val validationResult = "[]"

        whenever(parseService.validateSnippet(request)).thenReturn(validationResult)

        val result = snippetService.validateSnippet(request)

        assertEquals("[]", result) // Si es válido, no hay mensajes de error
    }

    @Test
    fun `getSnippet should return snippet if found`() {
        val id = "123"
        val snippet = Snippet(id, "Test Snippet", "user123", Conformance.PENDING, "Kotlin", "kt")

        whenever(snippetRepository.findById(id)).thenReturn(Optional.of(snippet))

        val result = snippetService.getSnippet(id)

        assertNotNull(result)
        assertEquals(snippet, result)
    }

    @Test
    fun `getSnippet should return null if snippet is not found`() {
        val id = "123"

        whenever(snippetRepository.findById(id)).thenReturn(Optional.empty())

        val result = snippetService.getSnippet(id)

        assertNull(result)
    }

    // Nuevos Tests para mejorar la cobertura

    @Test
    fun `helloParse should return response from parse service`() {
        // Arrange
        val responseMessage = "Hello Parse"
        whenever(parseService.hey()).thenReturn(responseMessage)

        // Act
        val result = snippetService.helloParse()

        // Assert
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(responseMessage, result.body)
    }

    @Test
    fun `helloPermissions should return response from permissions service`() {
        // Arrange
        val responseMessage = "Hello Permissions"
        whenever(permissionsService.hey()).thenReturn(responseMessage)

        // Act
        val result = snippetService.helloPermissions()

        // Assert
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(responseMessage, result.body)
    }

//    @Test
//    fun `getAllSnippetsByUser should return a list of snippets with authors`() {
//        // Arrange
//        val userId = "user123"
//        val username = "JohnDoe"
//        val snippetId = "123"
//        val snippet = Snippet(id = snippetId, name = "Test Snippet", userId = userId, conformance = Conformance.PENDING, languageName = "Kotlin", languageExtension = "kt")
//        val user = User(id = userId, nickname = username)
//        val snippetWithAuthor = SnippetService.SnippetWithAuthor(snippet, username)
//
//        whenever(permissionsService.getAllUserSnippets(userId)).thenReturn(listOf())
//        whenever(snippetRepository.findById(snippetId)).thenReturn(Optional.of(snippet))
//        whenever(auth0Service.getUserById(userId)).thenReturn(ResponseEntity.ok(user))
//
//        // Act
//        val result = snippetService.getAllSnippetsByUser(userId, username)
//
//        // Assert
//        assertNotNull(result)
//        if (result != null) {
//            assertEquals(1, result.size)
//        }
//        assertEquals(snippetWithAuthor, result?.get(0) ?: SnippetService.SnippetWithAuthor(Snippet(), ""))
//    }

    @Test
    fun `shareSnippet should return NOT_FOUND if snippet not found`() {
        // Arrange
        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val shareSnippetRequest = ShareSnippetRequest("nonExistentSnippetId", "user456")

        whenever(snippetRepository.findById(shareSnippetRequest.snippetId)).thenReturn(Optional.empty())

        // Act
        val response = snippetService.shareSnippet(token, shareSnippetRequest)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
