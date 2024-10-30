package com.github.teamverdeingsis.snippets.services

import com.github.teamverdeingsis.snippets.models.Snippet
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class PermissionsSerivce(private val restTemplate: RestTemplate, private val snippetService: SnippetService) {

    //tienen que pegarle al puerto donde esta el servicio de permisos y asi pegarle a los endpoints
    public fun getPermissions(userId: String, snippetId: String): String {
        return "Permissions for user $userId on snippet $snippetId"
    }

    public fun getAllUserSnippets(userId: String): List<Snippet> {
        val url = "http://localhost:8082/api/snippets/user/$userId/snippets"
        val response = restTemplate.getForEntity(url, Array<UUID>::class.java)
        if(!response.statusCode.is2xxSuccessful){
            //No snippets found
            throw RuntimeException("User with ID $userId not found")
        }
        val snippets = emptyList<Snippet>()
        for (id in response.body!!){
            val snippet= snippetService.getSnippet(id.toString())
            snippets.plus(snippet)
        }
        return snippets
    }

    
    public fun addPermission(userId: String, snippetId: String, permission: String): String {
        return "Permission $permission added for user $userId on snippet $snippetId"
    }

    public fun updatePermission(userId: String, snippetId: String, permission: String): String {
        return "Permission $permission updated for user $userId on snippet $snippetId"
    }

    public fun deletePermission(userId: String, snippetId: String): String {
        return "Permission deleted for user $userId on snippet $snippetId"
    }
    public fun getUsernameById(userId: String): String {
        try {
            val request = "http://localhost:8082/getUsernameById/$userId"
            return restTemplate.getForObject(request, String::class.java)!!
        }
        catch (e: Exception){
            throw RuntimeException("User with ID $userId not found")
        }
    }


}