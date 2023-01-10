package com.progressp.models.user

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object UsersTable : UUIDTable("users") {
    var email: Column<String> = varchar("email", 128).uniqueIndex()
    var password: Column<String> = varchar("password", 256)
    val username: Column<String> = varchar("username", 32).uniqueIndex()
    val role: Column<Int> = integer("role")
    val premium: Column<Int> = integer("premium")
    val passwordResetToken: Column<String> = varchar("password_reset_token", 128).default("")
    val emailVerifiedAt: Column<String> = varchar("email_verified_at", 128).default("")
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
    var updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(UsersTable)

    var email by UsersTable.email
    var username by UsersTable.username
    var password by UsersTable.password
    var role by UsersTable.role
    var premium by UsersTable.premium
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

    data class UpdateAdmin(
        val id: String,
        val email: String,
        val username: String,
        val role: Int,
        val premium: Int,
    )

    data class Login(
        val user: String,
        val password: String,
    )

    data class Page(
        val id: String,
        val email: String,
        val username: String,
        val role: Int,
        val premium: Int,
        val createdAt: LocalDateTime,
    ) {
        companion object {
            fun fromUserRow(row: User): Page = Page(
                id = row.id.toString(),
                email = row.email,
                username = row.username,
                role = row.role,
                premium = row.premium,
                createdAt = row.createdAt,
            )
        }
    }

    data class Register(
        val id: String?,
        val email: String,
        val username: String,
        val password: String?,
    )

    data class Response(
        val id: String,
        val username: String,
    )
}