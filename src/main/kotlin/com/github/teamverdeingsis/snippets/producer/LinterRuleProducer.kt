package com.github.teamverdeingsis.snippets.producer

import com.github.teamverdeingsis.snippets.models.SnippetMessage
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class LinterRuleProducer(
    @Value("\${stream.linter.key}") private val streamKey: String,
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {

    suspend fun publishSnippetMessage(userId: String, snippetId: String) {
        val message = SnippetMessage(userId, snippetId)
        redisTemplate.opsForStream<String, SnippetMessage>()
            .add(streamKey, mapOf("message" to message)).awaitSingle()
    }
}

