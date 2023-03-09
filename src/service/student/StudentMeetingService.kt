package com.progressp.service.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.Student
import com.progressp.models.student.StudentMeeting
import com.progressp.models.student.StudentSession
import com.progressp.models.student.StudentsMeetingsTable
import com.progressp.models.user.User
import com.progressp.util.Preconditions
import com.progressp.util.StudentMeetingNotFound
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
import com.progressp.util.StudentSessionNotFound
import com.progressp.util.getUserDataFromJWT
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.UUID

interface IStudentMeetingService {
    suspend fun userAll(token: String): ArrayList<StudentMeeting.Page>
    suspend fun userCreate(token: String, meetingProps: StudentMeeting.New): StudentMeeting.Response
    suspend fun userUpdate(token: String, meetingProps: StudentMeeting.New): StudentMeeting.Response
}

class StudentMeetingService(private val databaseFactory: IDatabaseFactory) : IStudentMeetingService {

    private fun getMeeting(id: String) =
        StudentMeeting.findById(UUID.fromString(id)) ?: throw StudentMeetingNotFound(id)

    override suspend fun userAll(token: String): ArrayList<StudentMeeting.Page> {
        val userId = getUserDataFromJWT(token, "id") as String
        val list = ArrayList<StudentMeeting.Page>()
        return databaseFactory.dbQuery {
            StudentMeeting.find { StudentsMeetingsTable.instructorId eq User.findById(UUID.fromString(userId))!!.id }
                .orderBy(StudentsMeetingsTable.createdAt to SortOrder.DESC).forEach {
                    list.add(
                        StudentMeeting.Page.fromDbRow(it)
                    )
                }
            list
        }
    }

    override suspend fun userCreate(token: String, meetingProps: StudentMeeting.New): StudentMeeting.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfSessionsExists(meetingProps.sessionId))
            throw StudentSessionNotFound(meetingProps.sessionId)
        if (!Preconditions(databaseFactory).checkIfStudentExists(meetingProps.studentId))
            throw StudentNotFound(meetingProps.studentId)
        if (!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, meetingProps.studentId))
            throw StudentNotYours(tokenUserId, meetingProps.studentId)

        return databaseFactory.dbQuery {
            val meeting = StudentMeeting.new {
                instructorId = User.findById(UUID.fromString(tokenUserId))!!.id
                studentId = Student.findById(UUID.fromString(meetingProps.studentId))!!.id
                sessionId = StudentSession.findById(UUID.fromString(meetingProps.sessionId))!!.id
                startAt = LocalDateTime.parse(meetingProps.startAt)
                endAt = LocalDateTime.parse(meetingProps.endAt)
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            StudentMeeting.Response.fromRow(meeting)
        }
    }

    override suspend fun userUpdate(token: String, meetingProps: StudentMeeting.New): StudentMeeting.Response {
        val tokenUserId = getUserDataFromJWT(token, "id") as String

        if (!Preconditions(databaseFactory).checkIfSessionsExists(meetingProps.sessionId))
            throw StudentSessionNotFound(meetingProps.sessionId)
        if (!Preconditions(databaseFactory).checkIfStudentExists(meetingProps.studentId))
            throw StudentNotFound(meetingProps.studentId)
        if (!Preconditions(databaseFactory).checkIfUserCanUpdateStudent(tokenUserId, meetingProps.studentId))
            throw StudentNotYours(tokenUserId, meetingProps.studentId)

        val meeting = databaseFactory.dbQuery {
            getMeeting(meetingProps.id!!)
        }

        return databaseFactory.dbQuery {
            meeting.apply {
                instructorId = User.findById(UUID.fromString(tokenUserId))!!.id
                studentId = Student.findById(UUID.fromString(meetingProps.studentId))!!.id
                sessionId = StudentSession.findById(UUID.fromString(meetingProps.sessionId))!!.id
                startAt = LocalDateTime.parse(meetingProps.startAt)
                endAt = LocalDateTime.parse(meetingProps.endAt)
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            StudentMeeting.Response.fromRow(meeting)
        }
    }
}
