package com.example.repositories

import com.example.entities.NotesEntity
import com.example.models.Note
import com.example.models.NoteRequest
import org.ktorm.database.Database
import org.ktorm.dsl.*

public class NotesRepository(var db: Database) {
    fun getAllNotes() : List<Note> {
        return db.from(NotesEntity).select()
            .map {
                val id = it[NotesEntity.id]
                val note = it[NotesEntity.note]
                Note(id ?: -1, note ?: "")
            }
    }

    fun getNoteById(id: Int) : Note? {
        return db.from(NotesEntity)
            .select()
            .where { NotesEntity.id eq id }
            .map {
                val id = it[NotesEntity.id]!!
                val note = it[NotesEntity.note]!!
                Note(id = id, note = note)
            }.firstOrNull()
    }

    fun createNote(text: String) : Int {
        return db.insert(NotesEntity) {
            set(it.note, text)
        }
    }

    fun updateNote(id: Int, updatedNote: NoteRequest): Int {
        return db.update(NotesEntity) {
            set(it.note, updatedNote.note)
            where {
                it.id eq id
            }
        }
    }

    fun deleteNote(id: Int): Int {
        return db.delete(NotesEntity){
            it.id eq id
        }
    }
}