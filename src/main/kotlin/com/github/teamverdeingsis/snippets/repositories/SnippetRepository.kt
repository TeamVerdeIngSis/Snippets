package com.github.teamverdeingsis.snippets.repositories

import com.github.teamverdeingsis.snippets.models.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface SnippetRepository : JpaRepository<Snippet, String> {
    // CRUD methods inherited from JpaRepository
    fun findByUserId(userId: String): List<Snippet>
}