package com.progressp.models.student

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object StudentsNotesTable : UUIDTable("students_notes") {
    val studentId: Column<EntityID<UUID>> = reference("student_id", StudentsTable, onDelete = ReferenceOption.CASCADE)
    val weight: Column<Double> = double("weight")
    val waist: Column<Double> = double("waist")
    val arms: Column<Double> = double("arms")
    val legs: Column<Double> = double("legs")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class StudentNote(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<StudentNote>(StudentsNotesTable)

    var studentId by StudentsNotesTable.studentId
    var weight by StudentsNotesTable.weight
    var waist by StudentsNotesTable.waist
    var arms by StudentsNotesTable.arms
    var legs by StudentsNotesTable.legs
    var updatedAt by StudentsNotesTable.updatedAt
    var createdAt by StudentsNotesTable.createdAt

    data class New(
        val id: String?,
        val studentId: String,
        val weight: Double,
        val waist: Double,
        val arms: Double,
        val legs: Double,
    )

    data class Response(
        val id: String,
        val studentId: String,
    ) {
        companion object {
            fun fromRow(row: StudentNote): Response = Response(
                id = row.id.toString(),
                studentId = row.studentId.toString(),
            )
        }
    }
}