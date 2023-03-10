package com.progressp.service.student

import com.progressp.data.MockData
import com.progressp.data.MockUUIDs
import com.progressp.database.dbService
import com.progressp.models.student.StudentsMeetingsTable
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import com.progressp.util.StudentMeetingNotFound
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
import com.progressp.util.StudentSessionNotFound
import com.progressp.util.progressJWT
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentMeetingServiceTest {

    private lateinit var studentService: StudentService
    private lateinit var sessionService: StudentSessionService
    private lateinit var meetingService: StudentMeetingService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        studentService = StudentService(dbService)
        sessionService = StudentSessionService(dbService)
        meetingService = StudentMeetingService(dbService)
        userService = UserService(dbService)
        runBlocking {
            dbService.dbQuery {
                StudentsTable.deleteAll()
                StudentsMeetingsTable.deleteAll()
                UsersTable.deleteAll()
            }
        }
    }

    @Test
    fun `user does not create a meeting if session does not exist`() {
        runBlocking {
            assertFailsWith(StudentSessionNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                meetingService.userCreate(userToken, MockData.newMeeting)
            }
        }
    }

    @Test
    fun `user does not create a meeting if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )

                meetingService.userCreate(
                    userToken, MockData.newMeeting.copy(
                        sessionId = sessionBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user does not create a meeting if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                meetingService.userCreate(
                    otherToken, MockData.newMeeting.copy(
                        sessionId = sessionBean.id,
                        studentId = studentBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user creates a meeting`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val sessionBean = sessionService.userCreate(
                userToken, MockData.newSession.copy(studentId = studentBean.id)
            )
            val result = meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    sessionId = sessionBean.id,
                    studentId = studentBean.id
                )
            )
            assertNotNull(result)
        }
    }

    @Test
    fun `user reads a page of results`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val sessionBean = sessionService.userCreate(
                userToken, MockData.newSession.copy(studentId = studentBean.id)
            )
            meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    sessionId = sessionBean.id,
                    studentId = studentBean.id
                )
            )
            meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    sessionId = sessionBean.id,
                    studentId = studentBean.id
                )
            )
            val result = meetingService.userAll(userToken)

            assertEquals(2, result.size)
        }
    }

    @Test
    fun `user does not update a meeting if session does not exist`() {
        runBlocking {
            assertFailsWith(StudentSessionNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                meetingService.userUpdate(userToken, MockData.newMeeting)
            }
        }
    }

    @Test
    fun `user does not update a meeting if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )

                meetingService.userUpdate(
                    userToken, MockData.newMeeting.copy(
                        sessionId = sessionBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user does not update a meeting if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                meetingService.userUpdate(
                    otherToken, MockData.newMeeting.copy(
                        sessionId = sessionBean.id,
                        studentId = studentBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user does not update a meeting if meeting does not exist`() {
        runBlocking {
            assertFailsWith(StudentMeetingNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )
                meetingService.userUpdate(
                    userToken,
                    MockData.newMeeting.copy(
                        sessionId = sessionBean.id,
                        studentId = studentBean.id,
                    )
                )
            }
        }
    }

    @Test
    fun `user updates a meeting`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val sessionBean = sessionService.userCreate(
                userToken, MockData.newSession.copy(studentId = studentBean.id)
            )
            val createdMeeting = meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    sessionId = sessionBean.id,
                    studentId = studentBean.id
                )
            )
            val updatedMeeting = meetingService.userUpdate(
                userToken,
                MockData.newMeeting.copy(
                    id = createdMeeting.id,
                    sessionId = sessionBean.id,
                    studentId = studentBean.id,
                    startAt = "2000-01-01T11:00:00"
                )
            )
            assertNotNull(updatedMeeting)
        }
    }
}
