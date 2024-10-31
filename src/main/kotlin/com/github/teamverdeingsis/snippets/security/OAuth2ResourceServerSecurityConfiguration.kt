package com.github.teamverdeingsis.snippets.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
@EnableWebSecurity
class OAuth2ResourceServerSecurityConfiguration(
    @Value("\${auth0.audience}") val audience: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") val issuer: String
) {

//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .authorizeHttpRequests {
//                it
//                    .requestMatchers(HttpMethod.GET, "/api/snippets").hasAuthority("SCOPE_read:snippets")
//                    .requestMatchers(HttpMethod.POST, "/api/snippets").hasAuthority("SCOPE_write:snippets")
//                    .requestMatchers(HttpMethod.GET, "/api/snippets/hello").permitAll()
//                    .anyRequest().authenticated()
//            }
//            .oauth2ResourceServer {
//                it.jwt()
//            }
//
//        println("Security configuration: ${http}")
//
//        return http.build()
//    }

@Bean
fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http
        .authorizeHttpRequests {
            it
                .anyRequest().authenticated()
        }
    println("Security configuration: ${http}")
    return http.build()
}
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build()
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)

        // Debugging JWT claims
//        jwtDecoder.setJwtValidator { jwt ->
//            println("JWT Audience: ${jwt.audience}")
//            println("JWT Claims: ${jwt.claims}")
//            DelegatingOAuth2TokenValidator(withIssuer, audienceValidator).validate(jwt)
//        }

        return jwtDecoder
    }

}
