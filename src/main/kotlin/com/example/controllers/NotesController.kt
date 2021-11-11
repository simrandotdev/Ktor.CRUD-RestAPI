package com.example.controllers

import com.example.models.NoteRequest
import com.example.models.NoteResponse
import com.example.repositories.NotesRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

public class NotesController(var repo: NotesRepository) {
    /**
     * Return all the notes from the Database
     */
    suspend fun getAllNotes(call: ApplicationCall) {
        val notes = repo.getAllNotes()
        call.respond(notes)
    }

    /**
     * Create the Note from the parameters sent in the body
     */
    suspend fun createNote(call: ApplicationCall) {
        val request = call.receive<NoteRequest>()
        val result = repo.createNote(request.note)

        if (result == 1) {
            // Send successfully response to the client
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(success = true, data = "Values has been successfully inserted")
            )
        } else {
            // Send failure response to the client
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Failed to insert values.")
            )
        }
    }

    /**
     * Get a single note with the id passed in the url if available
     */
    suspend fun getNoteById(call: ApplicationCall) {
        val id = call.parameters["id"]?.toInt() ?: -1

        val note = repo.getNoteById(id)

        if(note == null) {
            call.respond(
                HttpStatusCode.NotFound,
                NoteResponse(success = false, data = "Could not found note with  id = $id")
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(success = true, data = note)
            )
        }
    }


    /**
     * Update the note with the id passed in the url and new parameters in the body is available
     */
    suspend fun updateNote(call: ApplicationCall) {
        val id =call.parameters["id"]?.toInt() ?: -1
        val updatedNote =call.receive<NoteRequest>()

        val rowsEffected = repo.updateNote(id, updatedNote = updatedNote)

        if(rowsEffected == 1) {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(success = true, data = "Note has been updated")
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Note failed to update")
            )
        }
    }

    /**
     * Delete the note with the id passed in the url if available
     */
    suspend fun deleteNote(call: ApplicationCall) {
        val id =call.parameters["id"]?.toInt() ?: -1
        val rowsEffected = repo.deleteNote(id)

        if(rowsEffected == 1) {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(success = true, data = "Note has been delete")
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(success = false, data = "Note failed to delete")
            )
        }
    }
}