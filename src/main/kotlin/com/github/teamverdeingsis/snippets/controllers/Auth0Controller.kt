package com.github.teamverdeingsis.snippets.controllers

import com.github.teamverdeingsis.snippets.models.GetUserDTO
import com.github.teamverdeingsis.snippets.services.Auth0Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/snippets")
class Auth0Controller(private val auth0Service: Auth0Service) {

    @GetMapping("/api/auth0/users")
    fun getUsers(
        @RequestParam page: Int,
        @RequestParam perPage: Int,
        @RequestParam nickname: String
    ): ResponseEntity<List<GetUserDTO>> {
        return try {
            val users = auth0Service.getUsers(page, perPage, nickname)
            ResponseEntity.ok(users.body)
        } catch (e: Exception) {
            // Manejo de errores: podrías extenderlo según el tipo de excepciones que maneje tu servicio
            ResponseEntity.status(500).build()
        }
    }
}
