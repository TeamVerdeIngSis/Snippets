package com.github.teamverdeingsis.snippets.services

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate){
    public fun getAsset(id: String,directory:String): String {
        val assetServiceUrl = "http://localhost:8084/v1/asset/$directory/$id"
        try {
            return restTemplate.getForObject(assetServiceUrl, String::class.java)!!
        } catch (e: Exception) {
            throw RuntimeException("Asset with ID $id not found")
        }
    }
    public fun addAsset(id: String,directory:String,content:String): String {
        val assetServiceUrl = "http://localhost:8084/v1/asset/$directory/$id"
        try {
            restTemplate.postForObject(assetServiceUrl, content, String::class.java)!!
            return "Asset with ID $id added"
        } catch (e: Exception) {
            throw RuntimeException("Asset with ID $id not found")
        }
    }
    public fun updateAsset(id: String,directory:String,content:String):String {
        val assetServiceUrl = "http://localhost:8084/v1/asset/$directory/$id"
        try {
             restTemplate.put(assetServiceUrl, content, String::class.java)
            return "Asset with ID $id updated"
        } catch (e: Exception) {
            throw RuntimeException("Asset with ID $id not found")
        }
    }
    public fun deleteAsset(id: String,directory:String) {
        val assetServiceUrl = "http://localhost:8084/v1/asset/$directory/$id"
        try {
            return restTemplate.delete(assetServiceUrl)
        } catch (e: Exception) {
            throw RuntimeException("Asset with ID $id not found")
        }
    }
}