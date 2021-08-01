package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.UserEntity
import com.example.models.NoteResponse
import com.example.models.UserCredentials
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.*

fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database

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
    }
}