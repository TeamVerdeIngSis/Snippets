package com.github.teamverdeingsis.snippets

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.github.teamverdeingsis.snippets", "com.github.teamverdeingsis.snippets.security"])
class SnippetsApplication(
    @Value("{https://snippet-service-api}") val audience: String,
    @Value("{https://dev-ppmfishyt4u8fel3.us.auth0.com/}") val issuer: String
) {
    @PostConstruct
    fun init() {
        println("Audience: $audience")
        println("Issuer: $issuer")
    }
}

fun main(args: Array<String>) {
    runApplication<SnippetsApplication>(*args)
}