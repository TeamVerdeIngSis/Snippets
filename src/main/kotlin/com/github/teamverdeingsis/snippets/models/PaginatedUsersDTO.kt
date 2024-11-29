package com.github.teamverdeingsis.snippets.models

data class PaginatedUsersDTO(
    val users: List<GetUserDTO>,
    val total: Int,
)