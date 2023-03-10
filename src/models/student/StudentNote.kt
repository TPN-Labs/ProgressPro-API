package com.progressp.models.student

import com.progressp.config.DbContstants
import com.progressp.models.user.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object StudentsNotesTable : UUIDTable("students_notes") {
    var instructorId: Column<EntityID<UUID>> = reference(
        "instructor_id", UsersTable, onDelete = ReferenceOption.CASCADE
    )
    val studentId: Column<EntityID<UUID>> = reference("student_id", StudentsTable, onDelete = ReferenceOption.CASCADE)
    val measurementName: Column<String> = varchar("measurement_name", DbContstants.STRING_LENGTH)
    val measurementValue: Column<Double> = double("measurement_value")
    val tookAt: Column<LocalDate> = date("took_at").default(LocalDate.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class StudentNote(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudentNote>(StudentsNotesTable)

    var instructorId by StudentsNotesTable.instructorId
    var studentId by StudentsNotesTable.studentId
    var measurementName by StudentsNotesTable.measurementName
    var measurementValue by StudentsNotesTable.measurementValue
    var tookAt by StudentsNotesTable.tookAt
    var updatedAt by StudentsNotesTable.updatedAt
    var createdAt by StudentsNotesTable.createdAt

    data class New(
        val id: String?,
        val studentId: String,
        val measurementName: String,
        val measurementValue: Double,
        val tookAt: String,
    )

    data class Page(
        val id: String,
        val student: Student.Response,
        val measurementName: String,
        val measurementValue: Double,
        val tookAt: LocalDate,
    ) {
        companion object {
            fun fromDbRow(row: StudentNote): Page {
                val student = Student.findById(row.studentId)!!
                return Page(
                    id = row.id.toString(),
                    student = Student.Response(
                        id = row.studentId.toString(),
                        fullName = student.fullName,
                        avatar = student.avatar,
                    ),
                    measurementName = row.measurementName,
                    measurementValue = row.measurementValue,
                    tookAt = row.tookAt,
                )
            }
        }
    }

    data class Response(
        val id: String,
        val studentId: String,
        val measurementName: String,
        val measurementValue: Double,
    ) {
        companion object {
            fun fromRow(row: StudentNote): Response = Response(
                id = row.id.toString(),
                studentId = row.studentId.toString(),
                measurementName = row.measurementName,
                measurementValue = row.measurementValue
            )
        }
    }
}
