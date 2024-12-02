package com.github.teamverdeingsis.snippets.security

import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT

object AuthorizationDecoder {

    fun decode(authorization: String): String {
        val token = authorization.removePrefix("Bearer ")
        val decodedJWT = JWTParser.parse(token) as SignedJWT
        return decodedJWT.jwtClaimsSet.subject
    }

    fun decodeUsername(authorization: String): String {
        val token = authorization.removePrefix("Bearer ")
        val decodedJWT = JWTParser.parse(token) as SignedJWT
        return decodedJWT.jwtClaimsSet.getStringClaim("username")
    }
}
