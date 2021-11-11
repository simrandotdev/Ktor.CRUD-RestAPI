package com.example.routing

import com.example.controllers.NotesController
import com.example.db.DatabaseConnection
import com.example.repositories.NotesRepository
import io.ktor.application.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Application.notesRoutes(db: Database) {
    val notesRepository = NotesRepository(db)
    val notesController = NotesController(notesRepository)

    routing {
        get("/notes") {  notesController.getAllNotes(call)  }
        post("/notes") {  notesController.createNote(call)  }
        get("/notes/{id}") { notesController.getNoteById(call)  }
        put("/notes/{id}"){ notesController.updateNote(call) }
        delete("/notes/{id}"){ notesController.deleteNote(call)  }
    }
}