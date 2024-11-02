package com.github.teamverdeingsis.snippets.services


import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate){
    public fun addAsset(content:String,directory:String, assetId:String): ResponseEntity<String> {
        val assetServiceUrl = "http://asset_service:8080/v1/asset/$directory/$assetId"
        return restTemplate.postForEntity(assetServiceUrl, content, String::class.java)
    }
    public fun updateAsset(assetId: String,directory: String, content: String): ResponseEntity<String> {
        val assetServiceUrl = "http://asset_service:8080/v1/asset/$directory/$assetId"
        try {
            restTemplate.put(assetServiceUrl, content, String::class.java)
            return ResponseEntity.ok().body("Asset with ID $assetId updated")
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $assetId not found")
        }
    }

    public fun deleteAsset(assetId: String,directory: String): ResponseEntity<String> {
        val assetServiceUrl = "http://asset_service:8080/v1/asset/$directory/$assetId"
        try {
            restTemplate.delete(assetServiceUrl)
            return ResponseEntity.ok().body("Asset with ID $assetId deleted")
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $assetId not found")
        }
    }
    public fun getAsset(assetId: String,directory: String): ResponseEntity<String> {
        val assetServiceUrl = "http://asset_service:8080/v1/asset/$directory/$assetId"
        try {
            return restTemplate.getForEntity(assetServiceUrl, String::class.java)
        }
        catch (e: Exception){
            throw RuntimeException("Asset with ID $assetId not found")
        }
    }


}