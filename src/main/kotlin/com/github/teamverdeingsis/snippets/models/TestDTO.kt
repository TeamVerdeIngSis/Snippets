package com.github.teamverdeingsis.snippets.models

class TestDTO(test: Test) {
    val id: String = test.id
    val name: String = test.name
    val input: List<String> = test.input
    val output: List<String> = test.output
}