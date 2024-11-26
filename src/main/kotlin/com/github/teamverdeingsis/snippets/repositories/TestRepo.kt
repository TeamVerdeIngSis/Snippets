package com.github.teamverdeingsis.snippets.repositories;

import com.github.teamverdeingsis.snippets.models.Test
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;

@Repository
interface TestRepo : JpaRepository<Test, String> {
    fun findTestBySnippetId(snippetId: String): List<Test>
}
