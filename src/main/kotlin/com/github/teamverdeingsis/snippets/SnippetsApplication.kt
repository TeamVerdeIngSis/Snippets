package com.github.teamverdeingsis.snippets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv
@SpringBootApplication
class SnippetsApplication

fun main(args: Array<String>) {
    runApplication<SnippetsApplication>(*args)
}
