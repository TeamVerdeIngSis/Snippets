package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.CreatePermissionRequest
import com.github.teamverdeingsis.snippets.models.Permission
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class PermissionsServiceTest {

    private lateinit var permissionsService: PermissionsService
    private val restTemplate: RestTemplate = mock()

    @BeforeEach
    fun setUp() {
        permissionsService = PermissionsService(restTemplate)
    }

    @Test
    fun `getAllUserSnippets should return list of permissions if user has snippets`() {
        // Simula la respuesta del RestTemplate
        val userId = "user123"
        val snippetId = "snippet123"
        val permission = Permission(userId, snippetId, "READ", "1")
        val permissions = arrayOf(permission)
        val response = ResponseEntity(permissions, HttpStatus.OK)

        `when`(restTemplate.getForEntity(anyString(), eq(Array<Permission>::class.java)))
            .thenReturn(response)

        val result = permissionsService.getAllUserSnippets(userId)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(permission, result[0])
    }

    @Test
    fun `getAllUserSnippets should throw exception if no permissions found`() {
        val userId = "user123"
        val response = ResponseEntity(emptyArray<Permission>(), HttpStatus.OK)

        `when`(restTemplate.getForEntity(anyString(), eq(Array<Permission>::class.java)))
            .thenReturn(response)

        val result = permissionsService.getAllUserSnippets(userId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `addPermission should return success message if permission is added`() {
        val userId = "user123"
        val snippetId = "snippet123"
        val permission = "READ"
        val url = "http://permissions-service-infra:8080/api/permissions/create"
        val request = CreatePermissionRequest(userId, snippetId, permission)
        val responseMessage = "Permission added"
        val response = ResponseEntity(responseMessage, HttpStatus.OK)

        `when`(restTemplate.postForEntity(url, request, String::class.java))
            .thenReturn(response)

        val result = permissionsService.addPermission(userId, snippetId, permission)

        assertEquals(responseMessage, result)
    }

    @Test
    fun `addPermission should throw exception if request fails`() {
        val userId = "user123"
        val snippetId = "snippet123"
        val permission = "READ"
        val url = "http://permissions-service-infra:8080/api/permissions/create"
        val request = CreatePermissionRequest(userId, snippetId, permission)
        val response = ResponseEntity<String>(HttpStatus.BAD_REQUEST)

        `when`(restTemplate.postForEntity(url, request, String::class.java))
            .thenReturn(response)

        val exception = assertThrows<RuntimeException> {
            permissionsService.addPermission(userId, snippetId, permission)
        }

        assertEquals("Failed to share snippet with $userId", exception.message)
    }

    @Test
    fun `getPermissions should return permissions string`() {
        val userId = "user123"
        val snippetId = "snippet123"
        val expected = "Permissions for user $userId on snippet $snippetId"

        val result = permissionsService.getPermissions(userId, snippetId)

        assertEquals(expected, result)
    }

    @Test
    fun `hey should return hello message`() {
        val url = "http://permissions-service-infra:8080/api/permissions/helloNga"
        val responseMessage = "Hello, Nga!"
        val response = ResponseEntity(responseMessage, HttpStatus.OK)

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(responseMessage)

        val result = permissionsService.hey()

        assertEquals(responseMessage, result)
    }

    @Test
    fun `hey should return null if no response`() {
        val url = "http://permissions-service-infra:8080/api/permissions/helloNga"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(null)

        val result = permissionsService.hey()

        assertNull(result)
    }

    @Test
    fun `updatePermission should return success message`() {
        val userId = "user123"
        val snippetId = "snippet123"
        val permission = "READ"
        val expected = "Permission $permission updated for user $userId on snippet $snippetId"

        val result = permissionsService.updatePermission(userId, snippetId, permission)

        assertEquals(expected, result)
    }

    @Test
    fun `deletePermission should return success message`() {
        val userId = "user123"
        val snippetId = "snippet123"
        val expected = "Permission deleted for user $userId on snippet $snippetId"

        val result = permissionsService.deletePermission(userId, snippetId)

        assertEquals(expected, result)
    }

    @Test
    fun `getUsernameById should return username`() {
        val userId = "user123"
        val username = "john_doe"
        val url = "http://permissions-service-infra:8080/getUsernameById/$userId"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(username)

        val result = permissionsService.getUsernameById(userId)

        assertEquals(username, result)
    }

    @Test
    fun `getUsernameById should throw exception if user not found`() {
        val userId = "user123"
        val url = "http://permissions-service-infra:8080/getUsernameById/$userId"

        `when`(restTemplate.getForObject(url, String::class.java)).thenThrow(RuntimeException("User not found"))

        val exception = assertThrows<RuntimeException> {
            permissionsService.getUsernameById(userId)
        }

        assertEquals("User with ID $userId not found", exception.message)
    }
}
