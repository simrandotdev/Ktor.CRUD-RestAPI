package com.example.routing

import com.example.controllers.AuthController
import com.example.repositories.AuthRepository
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Application.authenticationRoutes(db: Database, config: HoconApplicationConfig) {

    val tokenManager = TokenManager(config)
    val authRepository = AuthRepository(db)
    val authController = AuthController(authRepository, tokenManager)


    routing {

        post("/register") {  authController.registerUser(call)  }

        /**
         * Log In the user with the sent username and password and send a JWT token back
         */
        post("/login") {
            authController.loginUser(call)
        }


        authenticate {
            /**
             * Protected route that returns the logged in users information.
             */
            get("/me") {
                val principle = call.principal<JWTPrincipal>()
                val username = principle!!.payload.getClaim("username").asString()
                val userId = principle!!.payload.getClaim("userId").asInt()
                call.respondText("Hello, $username with id: $userId")
            }
        }
    }
}