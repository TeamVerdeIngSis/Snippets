package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.GetUserDTO
import com.github.teamverdeingsis.snippets.services.Auth0Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth0/users")
class Auth0Controller(private val auth0Service: Auth0Service) {

    @GetMapping
    fun getUsers(
        @RequestParam page: Int,
        @RequestParam perPage: Int,
        @RequestParam(required = false, defaultValue = "") nickname: String
    ): ResponseEntity<List<GetUserDTO>> {
        return try {
            val users = auth0Service.getUsers(page, perPage, nickname)
            ResponseEntity.ok(users.body)
        } catch (e: Exception) {
            ResponseEntity.status(500).build()
        }
    }
}