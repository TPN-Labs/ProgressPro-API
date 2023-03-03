package com.progressp.service.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.Student
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.User
import com.progressp.util.*
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.UUID

interface IStudentService {
    suspend fun userAll(token: String): ArrayList<Student.Page>
    suspend fun userCreate(token: String, studentProps: Student.New): Student.Response
    suspend fun userUpdate(token: String, studentProps: Student.New): Student.Response
    suspend fun userDelete(token: String, studentProps: Student.Delete): Student.Response
}

class StudentService(private val databaseFactory: IDatabaseFactory) : IStudentService {

    private fun getStudent(id: String) = Student.findById(UUID.fromString(id)) ?: throw StudentNotFound(id)

    override suspend fun userAll(token: String): ArrayList<Student.Page> {
        val userId = getUserDataFromJWT(token, "id") as String
        val list = ArrayList<Student.Page>()
        return databaseFactory.dbQuery {
            Student.find { StudentsTable.instructorId eq User.findById(UUID.fromString(userId))!!.id }
                .orderBy(StudentsTable.createdAt to SortOrder.DESC).forEach {
                    list.add(
                        Student.Page.fromDbRow(it)
                    )
                }
            list
        }
    }
    
    override suspend fun userCreate(token: String, studentProps: Student.New): Student.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfStudentGenderExists(studentProps.gender))
            throw StudentGenderNotFound(tokenUserId, studentProps.gender)
        if (!Preconditions(databaseFactory).checkIfStudentAvatarIsValid(studentProps.avatar))
            throw StudentAvatarIsInvalid(tokenUserId, studentProps.avatar)
        if (!Preconditions(databaseFactory).checkIfStudentHeightIsValid(studentProps.height))
            throw StudentHeightIsInvalid(tokenUserId, studentProps.height)

        return databaseFactory.dbQuery {
            val student = Student.new {
                instructorId = User.findById(UUID.fromString(tokenUserId))!!.id
                fullName = studentProps.fullName
                gender = studentProps.gender
                height = studentProps.height
                avatar = studentProps.avatar
                knownFrom = LocalDate.parse(studentProps.knownFrom)
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            Student.Response.fromRow(student)
        }
    }

    override suspend fun userUpdate(token: String, studentProps: Student.New): Student.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfStudentGenderExists(studentProps.gender))
            throw StudentGenderNotFound(tokenUserId, studentProps.gender)
        if (!Preconditions(databaseFactory).checkIfStudentHeightIsValid(studentProps.height))
            throw StudentHeightIsInvalid(tokenUserId, studentProps.height)
        if (!Preconditions(databaseFactory).checkIfStudentAvatarIsValid(studentProps.avatar))
            throw StudentAvatarIsInvalid(tokenUserId, studentProps.avatar)
        if (!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, studentProps.id!!))
            throw StudentNotYours(tokenUserId, studentProps.id)

        return databaseFactory.dbQuery {
            val student = getStudent(studentProps.id)
            student.apply {
                fullName = studentProps.fullName
                gender = studentProps.gender
                height = studentProps.height
                avatar = studentProps.avatar
                knownFrom = LocalDate.parse(studentProps.knownFrom)
                updatedAt = LocalDateTime.now()
            }
            Student.Response.fromRow(student)
        }
    }

    override suspend fun userDelete(token: String, studentProps: Student.Delete): Student.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if(!Preconditions(databaseFactory).checkIfStudentExists(studentProps.id))
            throw StudentNotFound(studentProps.id)
        if(!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, studentProps.id))
            throw StudentNotYours(tokenUserId, studentProps.id)

        return databaseFactory.dbQuery {
            val student = Student.findById(UUID.fromString(studentProps.id))
            student!!.delete()
            Student.Response(
                id = student.id.toString(),
                fullName = student.fullName
            )
        }
    }
}