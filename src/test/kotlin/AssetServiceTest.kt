package com.github.teamverdeingsis.snippets.services

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@ExtendWith(SpringExtension::class)
class AssetServiceTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    @InjectMocks
    lateinit var assetService: AssetService

    // Test for addAsset method
    @Disabled
    @Test
    fun `addAsset should return success message`() {
        val content = "Some content"
        val directory = "snippets"
        val id = "123"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$id"

        // Mock the RestTemplate's put method to do nothing (since it's void)
        doNothing().`when`(restTemplate).postForObject(eq(assetServiceUrl), eq(content), eq(String::class.java))

        val result = assetService.addAsset(content, directory, id)

        // Assert the response message
        assertNotNull(result)
        assertEquals("Asset with ID $id added", result.body)
        verify(restTemplate).postForObject(eq(assetServiceUrl), eq(content), eq(String::class.java))
    }

    // Test for updateAsset method
    @Disabled
    @Test
    fun `updateAsset should return success message`() {
        val assetId = "123"
        val directory = "snippets"
        val content = "Updated content"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$assetId"

        // Mock the RestTemplate's put method to do nothing (since it's void)
        doNothing().`when`(restTemplate).postForObject(eq(assetServiceUrl), eq(content), eq(String::class.java))

        val result = assetService.updateAsset(assetId, directory, content)

        // Assert the response message
        assertNotNull(result)
        assertEquals("Asset with ID $assetId updated", result.body)
        verify(restTemplate).postForObject(eq(assetServiceUrl), eq(content), eq(String::class.java))
    }

    // Test for deleteAsset method
    @Test
    fun `deleteAsset should return success message`() {
        val snippetId = "123"
        val directory = "snippets"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$snippetId"

        // Mock the RestTemplate's delete method to do nothing (since it's void)
        doNothing().`when`(restTemplate).delete(eq(assetServiceUrl))

        val result = assetService.deleteAsset(snippetId, directory)

        // Assert the response message
        assertNotNull(result)
        assertEquals("Asset with ID $snippetId deleted", result.body)
        verify(restTemplate).delete(assetServiceUrl)
    }

    // Test for getAsset method
    @Test
    fun `getAsset should return asset content`() {
        val snippetId = "123"
        val directory = "snippets"
        val expectedContent = "Snippet content"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$snippetId"

        // Mock the RestTemplate's getForObject method
        whenever(restTemplate.getForObject(eq(assetServiceUrl), eq(String::class.java)))
            .thenReturn(expectedContent)

        val result = assetService.getAsset(snippetId, directory)

        // Assert the response content
        assertEquals(expectedContent, result)
        verify(restTemplate).getForObject(eq(assetServiceUrl), eq(String::class.java))
    }

    // Test for assetExists method
    @Test
    fun `assetExists should return true if asset exists`() {
        val snippetId = "123"
        val directory = "snippets"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$snippetId"

        // Mock the RestTemplate's getForObject method to simulate asset existence
        whenever(restTemplate.getForObject(eq(assetServiceUrl), eq(String::class.java)))
            .thenReturn("Asset content")

        val result = assetService.assetExists(directory, snippetId)

        // Assert that the asset exists
        assertTrue(result)
        verify(restTemplate).getForObject(eq(assetServiceUrl), eq(String::class.java))
    }

    @Test
    fun `assetExists should return false if asset does not exist`() {
        val snippetId = "123"
        val directory = "snippets"
        val assetServiceUrl = "http://asset-service-infra:8080/v1/asset/$directory/$snippetId"

        // Mock the RestTemplate's getForObject method to simulate asset not found
        whenever(restTemplate.getForObject(eq(assetServiceUrl), eq(String::class.java)))
            .thenThrow(RuntimeException("Asset not found"))

        val result = assetService.assetExists(directory, snippetId)

        // Assert that the asset does not exist
        assertFalse(result)
        verify(restTemplate).getForObject(eq(assetServiceUrl), eq(String::class.java))
    }
}
