package com.progressp.models.student

import com.progressp.models.user.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object StudentsMeetingsTable : UUIDTable("students_meetings") {
    val instructorId: Column<EntityID<UUID>> = reference(
        "instructor_id", UsersTable, onDelete = ReferenceOption.CASCADE
    )
    val studentId: Column<EntityID<UUID>> = reference(
        "student_id", StudentsTable, onDelete = ReferenceOption.CASCADE
    )
    val firstInMonth: Column<Boolean> = bool("first_in_month").default(false)
    val startAt: Column<LocalDateTime> = datetime("start_at").default(LocalDateTime.now())
    val endAt: Column<LocalDateTime> = datetime("end_at").default(LocalDateTime.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class StudentMeeting(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudentMeeting>(StudentsMeetingsTable)

    var instructorId by StudentsMeetingsTable.instructorId
    var studentId by StudentsMeetingsTable.studentId
    var startAt by StudentsMeetingsTable.startAt
    var endAt by StudentsMeetingsTable.endAt
    var firstInMonth by StudentsMeetingsTable.firstInMonth
    var updatedAt by StudentsMeetingsTable.updatedAt
    var createdAt by StudentsMeetingsTable.createdAt

    data class New(
        val id: String?,
        val studentId: String,
        val sessionId: String,
        val firstInMonth: Boolean,
        val startAt: String,
        val endAt: String,
    )

    data class Response(
        val id: String,
        val studentId: String,
    ) {
        companion object {
            fun fromRow(row: StudentMeeting): Response = Response(
                id = row.id.toString(),
                studentId = row.studentId.toString(),
            )
        }
    }

    data class Page(
        val id: String,
        val student: Student.Response,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        val firstInMonth: Boolean,
    ) {
        companion object {
            fun fromDbRow(row: StudentMeeting): Page {
                val student = Student.findById(row.studentId)!!
                return Page(
                    id = row.id.toString(),
                    student = Student.Response(
                        id = row.studentId.toString(),
                        fullName = student.fullName,
                        avatar = student.avatar,
                    ),
                    startAt = row.startAt,
                    endAt = row.endAt,
                    firstInMonth = row.firstInMonth
                )
            }
        }
    }
}
