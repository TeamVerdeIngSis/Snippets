package com.github.teamverdeingsis.snippets.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.teamverdeingsis.snippets.models.SnippetMessage
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class FormattingRuleProducer(
    @Value("\${stream.formattingKey}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): ProductCreatedProducer, RedisStreamProducer(streamKey, redis)  {
    override suspend fun publishEvent(authorization: String, snippetId: String) {

        // Crear el mensaje y serializarlo
        val message = SnippetMessage(authorization, snippetId)
        val serializedMessage = objectMapper.writeValueAsString(message)

        // Publicar el mensaje serializado

        emit(serializedMessage).awaitSingle()
    }

}

