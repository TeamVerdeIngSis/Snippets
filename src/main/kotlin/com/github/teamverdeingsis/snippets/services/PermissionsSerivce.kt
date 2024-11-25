package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.CreatePermissionRequest
import com.github.teamverdeingsis.snippets.models.Permission
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class PermissionsSerivce(private val restTemplate: RestTemplate) {

    //tienen que pegarle al puerto donde esta el servicio de permisos y asi pegarle a los endpoints
    public fun getPermissions(userId: String, snippetId: String): String {
        return "Permissions for user $userId on snippet $snippetId"
    }

    public fun getAllUserSnippets(userId: String): List<Permission> {

        val url = "http://localhost:8082/api/permissions/user/$userId"
        val response = restTemplate.getForEntity(url, Array<Permission>::class.java)
        if(!response.statusCode.is2xxSuccessful){

            throw RuntimeException("User with ID $userId not found")
        }
        else if(response.body!!.isEmpty()){
            return emptyList()
        }
        return response.body!!.toList()
    }

    public fun hey(): String? {
        val url = "http://permissionsServiceInfra:8082/api/permissions/helloNga"
        println("llegue a la funcion")
        val response = restTemplate.getForObject(url, String::class.java)
        if (response == null){
            println("nada")
        }
        println("Response: $response")
        return response
    }

    public fun addPermission(userId: String, snippetId: String, permission: String): String {
        val url = "http://permissionsServiceInfra:8082/api/permissions/create"
        val request = CreatePermissionRequest(userId, snippetId, permission)

        val response = restTemplate.postForEntity(url, request, String::class.java)
        if(!response.statusCode.is2xxSuccessful){
            throw RuntimeException("User with ID $userId not found")
        }
        return response.body!!

    }

    public fun updatePermission(userId: String, snippetId: String, permission: String): String {
        return "Permission $permission updated for user $userId on snippet $snippetId"
    }

    public fun deletePermission(userId: String, snippetId: String): String {
        return "Permission deleted for user $userId on snippet $snippetId"
    }
    public fun getUsernameById(userId: String): String {
        try {
            val request = "http://permissionsServiceInfra:8082/getUsernameById/$userId"
            return restTemplate.getForObject(request, String::class.java)!!
        }
        catch (e: Exception){
            throw RuntimeException("User with ID $userId not found")
        }
    }
}