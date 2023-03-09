package com.progressp.models.user

import com.progressp.config.DbContstants.LARGE_STRING_LENGTH
import com.progressp.config.DbContstants.MEDIUM_STRING_LENGTH
import com.progressp.config.DbContstants.STRING_LENGTH
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object UsersTable : UUIDTable("users") {
    var email: Column<String> = varchar("email", MEDIUM_STRING_LENGTH).uniqueIndex()
    var password: Column<String> = varchar("password", LARGE_STRING_LENGTH)
    val username: Column<String> = varchar("username", STRING_LENGTH).uniqueIndex()
    val role: Column<Int> = integer("role")
    val premium: Column<Int> = integer("premium")
    val passwordResetToken: Column<String> = varchar("password_reset_token", MEDIUM_STRING_LENGTH).default("")
    val emailVerifiedAt: Column<String> = varchar("email_verified_at", MEDIUM_STRING_LENGTH).default("")
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
