package com.example

import com.example.db.DatabaseConnection
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.ktorm.database.Database

fun main() {
    embeddedServer(Netty, port = getPORT(), host = "0.0.0.0") {
        val config = HoconApplicationConfig(ConfigFactory.load())
        val tokenManager = TokenManager(config)
        val db = DatabaseConnection.database

        install(Authentication) {
            jwt {  verifyJWT(tokenManager, config)  }
        }
        install(ContentNegotiation) {  json()  }
        configureRouting(db, config)
    }.start(wait = true)
}

private fun JWTAuthenticationProvider.Configuration.verifyJWT(
    tokenManager: TokenManager,
    config: HoconApplicationConfig
) {
    verifier(tokenManager.verifyJWTToken())
    realm = config.property("realm").getString()
    validate { jwtCredential ->
        if (jwtCredential.payload.getClaim("username").asString().isNotEmpty()) {
            JWTPrincipal(jwtCredential.payload)
        } else {
            null
        }
    }
}

fun getPORT(): Int {
    var PORT = 5555

    if (System.getenv("PORT") != null) {
        PORT = System.getenv("PORT").toInt()
    }
    return PORT
}