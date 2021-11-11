package com.example.controllers

import com.example.models.NoteResponse
import com.example.models.UserCredentials
import com.example.repositories.AuthRepository
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import org.mindrot.jbcrypt.BCrypt

class AuthController(private val repo: AuthRepository, private val tokenManager: TokenManager) {

    /**
     * Registers the user with the send username and password
     */
    suspend fun registerUser(call: ApplicationCall) {
        val userCredentials = call.receive<UserCredentials>()

        if (!userCredentials.isValidCredentials()) {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Username should be greater than or equal to 3 and password should be greater than or equal to 8")
            )
            return
        }

        val username = userCredentials.username.toLowerCase()
        val password = userCredentials.hashedPassword()

        // Check if username already exists
        val user = repo.checkIfUserExists(username)

        if(user != null) {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "User already exists, please try a different username")
            )
            return
        }

        repo.createUser(username, password)

        call.respond(
            HttpStatusCode.Created,
            NoteResponse(success = true, data = "User has been successfully created")
        )
    }

    suspend fun loginUser(call: ApplicationCall) {
        val userCredentials = call.receive<UserCredentials>()

        if (!userCredentials.isValidCredentials()) {
            call.respond(HttpStatusCode.BadRequest,
                NoteResponse(success = false,
                    data = "Username should be greater than or equal to 3 and password should be greater than or equal to 8"))
            return
        }

        val username = userCredentials.username.toLowerCase()
        val password = userCredentials.password

        // Check if user exists
        val user = repo.checkIfUserExists(username)

        if(user == null) {
            call.respond(HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Invalid username or password."))
            return
        }

        val doesPasswordMatch = BCrypt.checkpw(password, user?.password)
        if(!doesPasswordMatch) {
            call.respond(HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Invalid username or password."))
            return
        }

        val token = tokenManager.generateJWTToken(user)
        call.respond(
            HttpStatusCode.OK,
            NoteResponse(success = true, data = token)
        )
    }
}
