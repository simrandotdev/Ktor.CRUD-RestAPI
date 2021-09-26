package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.serialization.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val config = HoconApplicationConfig(ConfigFactory.load())
        val myRealm = config.property("realm").getString()
        val audience = config.property("audience").getString()
        val issuer = config.property("issuer").getString()
        val secret = config.property("secret").getString()

        install(Authentication) {
            jwt("auth-jwt") {
                realm = myRealm
                verifier(
                    JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build())

                validate { credential ->
                       if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                           JWTPrincipal(credential.payload)
                       } else {
                           null
                       }
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
        configureRouting()
    }.start(wait = true)
}
