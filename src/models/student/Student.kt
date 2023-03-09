package com.progressp.models.student

import com.progressp.config.DbContstants.STRING_LENGTH
import com.progressp.models.user.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object StudentsTable : UUIDTable("students") {
    val instructorId: Column<EntityID<UUID>> = reference(
        "instructor_id", UsersTable, onDelete = ReferenceOption.CASCADE
    )
    val fullName: Column<String> = varchar("full_name", STRING_LENGTH)
    val gender: Column<Int> = integer("gender")
    val avatar: Column<Int> = integer("avatar")
    val height: Column<Double> = double("height")
    val knownFrom: Column<LocalDate> = date("known_from").default(LocalDate.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class Student(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Student>(StudentsTable)

    var instructorId by StudentsTable.instructorId
    var fullName by StudentsTable.fullName
    var gender by StudentsTable.gender
    var avatar by StudentsTable.avatar
    var height by StudentsTable.height
    var knownFrom by StudentsTable.knownFrom
    var updatedAt by StudentsTable.updatedAt
    var createdAt by StudentsTable.createdAt

    data class New(
        val id: String?,
        val fullName: String,
        val avatar: Int,
        val gender: Int,
        val height: Double,
        val knownFrom: String,
    )

    data class Delete(
        val id: String,
    )

    data class Page(
        val id: String,
        val fullName: String,
        val avatar: Int,
        val gender: Int,
        val height: Double,
        val knownFrom: LocalDate,
    ) {
        companion object {
            fun fromDbRow(row: Student): Page = Page(
                id = row.id.toString(),
                fullName = row.fullName,
                avatar = row.avatar,
                gender = row.gender,
                height = row.height,
                knownFrom = row.knownFrom,
            )
        }
    }

    data class Response(
        val id: String,
        val fullName: String,
        val avatar: Int,
    ) {
        companion object {
            fun fromRow(row: Student): Response = Response(
                id = row.id.toString(),
                fullName = row.fullName,
                avatar = row.avatar,
            )
        }
    }
}

enum class StudentGender(val code: Int) {
    MALE(1),
    FEMALE(2),
    OTHER(3),
}
