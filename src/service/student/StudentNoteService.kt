package com.progressp.service.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.Student
import com.progressp.models.student.StudentNote
import com.progressp.models.student.StudentsNotesTable
import com.progressp.models.student.StudentsNotesTable.instructorId
import com.progressp.models.user.User
import com.progressp.util.MeasurementCodeNotFound
import com.progressp.util.Preconditions
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
import com.progressp.util.StudentNoteNotFound
import com.progressp.util.getUserDataFromJWT
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.UUID

interface IStudentNoteService {
    suspend fun userAll(token: String): ArrayList<StudentNote.Page>
    suspend fun userCreate(token: String, noteProps: StudentNote.New): StudentNote.Response
    suspend fun userUpdate(token: String, noteProps: StudentNote.New): StudentNote.Response
}

class StudentNoteService(private val databaseFactory: IDatabaseFactory) : IStudentNoteService {

    private fun getNote(id: String) =
        StudentNote.findById(UUID.fromString(id)) ?: throw StudentNoteNotFound(id)

    override suspend fun userAll(token: String): ArrayList<StudentNote.Page> {
        val userId = getUserDataFromJWT(token, "id") as String
        val list = ArrayList<StudentNote.Page>()
        return databaseFactory.dbQuery {
            StudentNote.find { instructorId eq User.findById(UUID.fromString(userId))!!.id }
                .orderBy(StudentsNotesTable.createdAt to SortOrder.DESC).forEach {
                    list.add(
                        StudentNote.Page.fromDbRow(it)
                    )
                }
            list
        }
    }

    override suspend fun userCreate(token: String, noteProps: StudentNote.New): StudentNote.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfStudentExists(noteProps.studentId))
            throw StudentNotFound(noteProps.studentId)
        if (!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, noteProps.studentId))
            throw StudentNotYours(tokenUserId, noteProps.studentId)
        if (!Preconditions(databaseFactory).checkIfMeasurementExists(noteProps.measurementName))
            throw MeasurementCodeNotFound(noteProps.measurementName)


        return databaseFactory.dbQuery {
            val note = StudentNote.new {
                instructorId = User.findById(UUID.fromString(tokenUserId))!!.id
                studentId = Student.findById(UUID.fromString(noteProps.studentId))!!.id
                measurementName = noteProps.measurementName.lowercase()
                measurementValue = noteProps.measurementValue
                tookAt = LocalDate.parse(noteProps.tookAt)
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            StudentNote.Response.fromRow(note)
        }
    }

    override suspend fun userUpdate(token: String, noteProps: StudentNote.New): StudentNote.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfMeasurementExists(noteProps.measurementName))
            throw MeasurementCodeNotFound(noteProps.measurementName)
        if (!Preconditions(databaseFactory).checkIfStudentExists(noteProps.studentId))
            throw StudentNotFound(noteProps.studentId)
        if (!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, noteProps.studentId))
            throw StudentNotYours(tokenUserId, noteProps.studentId)

        val note = databaseFactory.dbQuery {
            getNote(noteProps.id!!)
        }

        return databaseFactory.dbQuery {
            note.apply {
                instructorId = User.findById(UUID.fromString(tokenUserId))!!.id
                studentId = Student.findById(UUID.fromString(noteProps.studentId))!!.id
                measurementName = noteProps.measurementName.lowercase()
                measurementValue = noteProps.measurementValue
                tookAt = LocalDate.parse(noteProps.tookAt)
                updatedAt = LocalDateTime.now()
            }
            StudentNote.Response.fromRow(note)
        }
    }
}
