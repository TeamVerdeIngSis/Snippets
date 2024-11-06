package com.github.teamverdeingsis.snippets.services


import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class AssetService(private val restTemplate: RestTemplate){
    public fun addAsset(content:String,directory:String, snippetId:String): ResponseEntity<String> {
        println("HEYHEYHEY")
        val dir = directory
        val snipId = snippetId
        println(dir)
        println(snipId)
        if (assetExists(directory,snippetId)) {
            println("NONONONONO")
            throw RuntimeException("Asset with ID $snippetId already exists")
        }
        val assetServiceUrl = "http://localhost:8080/v1/asset/$dir/$snipId"
        println("CCCCCC")
        restTemplate.put(assetServiceUrl, content, String::class.java)
        return ResponseEntity.ok().body("Asset with ID $snippetId added")

    }
    public fun updateAsset(assetId: String,directory: String, content: String): ResponseEntity<String> {
        val assetServiceUrl = "http://localhost:8080/v1/asset/$directory/$assetId"
        try {
            restTemplate.put(assetServiceUrl, content, String::class.java)
            return ResponseEntity.ok().body("Asset with ID $assetId updated")
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $assetId not found")
        }
    }

    public fun deleteAsset(snippetId: String,directory: String): ResponseEntity<String> {
        val assetServiceUrl = "http://localhost:8080/v1/asset/$directory/$snippetId"
        try {
            restTemplate.delete(assetServiceUrl)
            return ResponseEntity.ok().body("Asset with ID $snippetId deleted")
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $snippetId not found")
        }
    }
    public fun getAsset(snippetId: String,directory: String): String?{
        val assetServiceUrl = "http://localhost:8080/v1/asset/$directory/$snippetId"
        try {
            return restTemplate.getForObject(assetServiceUrl, String::class.java)
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $snippetId not found")
        }
    }
    fun assetExists(directory: String,snippetId: String): Boolean {
        println("BOCABOCABOCA")
        val assetServiceUrl = "http://localhost:8080/v1/asset/$directory/$snippetId"
        try {
            restTemplate.getForObject(assetServiceUrl, String::class.java)
            return true
        }
        catch (e: Exception){
            return false
        }
    }


}