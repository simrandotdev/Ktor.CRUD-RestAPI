package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.UserEntity
import com.example.models.NoteResponse
import com.example.models.User
import com.example.models.UserCredentials
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {
        post("/register") {
            val userCredentials = call.receive<UserCredentials>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(HttpStatusCode.BadRequest,
                NoteResponse(success = false,
                    data = "Username should be greater than or equal to 3 and password should be greater than or equal to 8"))
                return@post
            }

            val username = userCredentials.username.toLowerCase()
            val password = userCredentials.hashedPassword()

            // Check if username already exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map { it[UserEntity.username] }
                .firstOrNull()

            if(user != null) {
                call.respond(HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "User already exists, please try a different username"))
                return@post
            }

            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }

            call.respond(
                HttpStatusCode.Created,
                NoteResponse(success = true, data = "User has been successfully created")
            )
        }


        post("/login") {
            val userCredentials = call.receive<UserCredentials>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(HttpStatusCode.BadRequest,
                    NoteResponse(success = false,
                        data = "Username should be greater than or equal to 3 and password should be greater than or equal to 8"))
                return@post
            }

            val username = userCredentials.username.toLowerCase()
            val password = userCredentials.password

            // Check if user exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password =  it[UserEntity.password]!!
                    User(id, username, password)
                }.firstOrNull()

            if(user == null) {
                call.respond(HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "Invalid username or password."))
                return@post
            }

            val doesPasswordMatch = BCrypt.checkpw(password, user?.password)
            if(!doesPasswordMatch) {
                call.respond(HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "Invalid username or password."))
                return@post
            }

            val token = tokenManager.generateJWTToken(user)
            call.respond(HttpStatusCode.OK,
            NoteResponse(success = true, data = token))
        }

        authenticate {
            get("/me") {
                val principle = call.principal<JWTPrincipal>()
                val username = principle!!.payload.getClaim("username").asString()
                val userId = principle!!.payload.getClaim("userId").asInt()
                call.respondText("Hello, $username with id: $userId")
            }
        }
    }
}