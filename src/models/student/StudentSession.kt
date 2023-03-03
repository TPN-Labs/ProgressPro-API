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

object StudentsSessionsTable : UUIDTable("students_sessions") {
    val studentId: Column<EntityID<UUID>> = reference("student_id", StudentsTable, onDelete = ReferenceOption.CASCADE)
    val instructorId: Column<EntityID<UUID>> = reference("instructor_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val status: Column<Int> = integer("status")
    val unit: Column<Int> = integer("unit")
    val meetings: Column<Int> = integer("meetings")
    val value: Column<Int> = integer("value")
    val currencyCode: Column<String> = varchar("currency_code", 8).default("USD")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class StudentSession(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudentSession>(StudentsSessionsTable)

    var studentId by StudentsSessionsTable.studentId
    var instructorId by StudentsSessionsTable.instructorId
    var status by StudentsSessionsTable.status
    var unit by StudentsSessionsTable.unit
    var meetings by StudentsSessionsTable.meetings
    var value by StudentsSessionsTable.value
    var currencyCode by StudentsSessionsTable.currencyCode
    var updatedAt by StudentsSessionsTable.updatedAt
    var createdAt by StudentsSessionsTable.createdAt

    data class New(
        val id: String?,
        val studentId: String,
        val status: Int,
        val value: Int,
        val meetings: Int,
        val currencyCode: String,
    )

    data class Response(
        val id: String,
        val status: Int,
        val unit: Int,
    ) {
        companion object {
            fun fromDbRow(row: StudentSession): Response = Response(
                id = row.id.toString(),
                status = row.status,
                unit = row.unit,
            )
        }
    }

    data class Page(
        val id: String,
        val student: Student.Response,
        val status: Int,
        val unit: Int,
        val value: Int,
        val meetings: Int,
        val currencyCode: String,
    ) {
        companion object {
            fun fromDbRow(row: StudentSession): Page {
                val student = Student.findById(row.studentId)!!
                return Page(
                    id = row.id.toString(),
                    student = Student.Response(
                        id = row.studentId.toString(),
                        fullName = student.fullName,
                    ),
                    status = row.status,
                    unit = row.unit,
                    value = row.value,
                    meetings = row.meetings,
                    currencyCode = row.currencyCode,
                )
            }
        }
    }
}

enum class StudentSessionStatus(val code: Int) {
    STARTED(1),
    PAID(2),
    CLOSED(3),
    ARCHIVED(4),
}