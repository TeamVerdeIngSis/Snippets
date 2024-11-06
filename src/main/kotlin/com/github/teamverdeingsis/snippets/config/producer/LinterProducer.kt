package com.github.teamverdeingsis.snippets.config.producer


import jdk.jfr.internal.OldObjectSample.emit
import kotlinx.coroutines.reactor.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.austral.ingsis.demo.consumer.ProductCreated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

interface LinterProducer {
    suspend fun publishEvent(request: ProducerRequest)
}

@Component
class RedisLinterProducer @Autowired constructor(
    @Value("\${stream.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : LinterProducer, RedisStreamProducer(streamKey, redis) {

    override suspend fun publishEvent(request: ProducerRequest) {
        println("Publishing on stream: $streamKey")
        val product = ProductCreated(name)
        emit(product).awaitSingle()
    }
}