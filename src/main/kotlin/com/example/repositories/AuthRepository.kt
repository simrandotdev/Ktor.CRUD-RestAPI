package com.example.repositories

import com.example.entities.UserEntity
import com.example.models.User
import com.example.utils.TokenManager
import org.ktorm.database.Database
import org.ktorm.dsl.*

class AuthRepository(var db: Database) {
    fun checkIfUserExists(username: String) : User? {
        return db.from(UserEntity)
            .select()
            .where { UserEntity.username eq username }
            .map {
                val id = it[UserEntity.id]!!
                val username = it[UserEntity.username]!!
                val password =  it[UserEntity.password]!!
                User(id, username, password)
            }.firstOrNull()
    }

    fun createUser(username: String, password: String) {
        db.insert(UserEntity) {
            set(it.username, username)
            set(it.password, password)
        }
    }
}
