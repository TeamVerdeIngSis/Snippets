package com.github.teamverdeingsis.snippets.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

// From experiments, this seems to be needed when using spring-webflux and not needed when using spring-mvc
@Configuration
class ConnectionFactory(@Value("\${spring.data.redis.host}") private val hostName: String, @Value("\${spring.data.redis.port}") private val port: Int) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(
            RedisStandaloneConfiguration(hostName, port),
        )
    }
}
