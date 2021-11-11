package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import io.ktor.config.*
import java.util.*

class TokenManager(config: HoconApplicationConfig) {
    private val audience = config.property("audience").getString()
    private val secret = config.property("secret").getString()
    private val issuer = config.property("issuer").getString()
    private val expirationDate = System.currentTimeMillis() + 600000;

    /***
     * Takes in the user object and returns a JWT token with username and userId embedded in it.
     */
    fun generateJWTToken(user: User): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", user.username)
        .withClaim("userId", user.id)
        .withExpiresAt(Date(expirationDate))
        .sign(Algorithm.HMAC256(secret))

    /**
     * Verifies if the passed in JWT token in the request is valid or not
     */
    fun verifyJWTToken(): JWTVerifier = JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
}