package com.progressp.service.student

import com.progressp.data.MockData
import com.progressp.data.MockUUIDs
import com.progressp.database.dbService
import com.progressp.models.student.Student
import com.progressp.models.student.StudentSession
import com.progressp.models.student.StudentSessionStatus
import com.progressp.models.student.StudentsSessionsTable
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
import com.progressp.util.StudentSessionNotFound
import com.progressp.util.StudentSessionStatusInvalid
import com.progressp.util.StudentSessionTotalInvalid
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
class StudentSessionServiceTest {

    private lateinit var studentService: StudentService
    private lateinit var sessionService: StudentSessionService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        studentService = StudentService(dbService)
        sessionService = StudentSessionService(dbService)
        userService = UserService(dbService)
        runBlocking {
            dbService.dbQuery {
                StudentsTable.deleteAll()
                StudentsSessionsTable.deleteAll()
                UsersTable.deleteAll()
            }
        }
    }

    @Test
    fun `user does not create a session if price is invalid`() {
        runBlocking {
            assertFailsWith(StudentSessionTotalInvalid::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userCreate(userToken, MockData.newSession.copy(price = -1))
            }
        }
    }

    @Test
    fun `user does not create a session if meetings is invalid`() {
        runBlocking {
            assertFailsWith(StudentSessionTotalInvalid::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userCreate(userToken, MockData.newSession.copy(meetings = -1))
            }
        }
    }

    @Test
    fun `user does not create a session if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userCreate(userToken, MockData.newSession)
            }
        }
    }

    @Test
    fun `user does not create a session if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                sessionService.userCreate(otherToken, MockData.newSession.copy(studentId = studentBean.id))
            }
        }
    }

    @Test
    fun `user creates a session`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val result = sessionService.userCreate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            assertNotNull(result)
        }
    }

    @Test
    fun `user creates a session and the status is STARTED`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val result = sessionService.userCreate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            assertEquals(result.status, StudentSessionStatus.STARTED.code)
        }
    }

    @Test
    fun `user reads a page of results`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)

            sessionService.userCreate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            sessionService.userCreate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            val result = sessionService.userAll(userToken)

            assertEquals(2, result.size)
        }
    }

    @Test
    fun `user does not update a session if price is invalid`() {
        runBlocking {
            assertFailsWith(StudentSessionTotalInvalid::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userUpdate(userToken, MockData.newSession.copy(price = -1))
            }
        }
    }

    @Test
    fun `user does not update a session if meetings is invalid`() {
        runBlocking {
            assertFailsWith(StudentSessionTotalInvalid::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userUpdate(userToken, MockData.newSession.copy(meetings = -1))
            }
        }
    }

    @Test
    fun `user does not update a session if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userUpdate(userToken, MockData.newSession)
            }
        }
    }

    @Test
    fun `user does not update a session if status does not exist`() {
        runBlocking {
            assertFailsWith(StudentSessionStatusInvalid::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                sessionService.userUpdate(userToken, MockData.newSession.copy(status = 10))
            }
        }
    }

    @Test
    fun `user does not update a session if session does not exist`() {
        runBlocking {
            assertFailsWith(StudentSessionNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                sessionService.userUpdate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            }
        }
    }

    @Test
    fun `user does not update a session if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(
                    userToken, MockData.newStudent
                )
                val sessionBean = sessionService.userCreate(
                    userToken, MockData.newSession.copy(studentId = studentBean.id)
                )

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                sessionService.userUpdate(
                    otherToken, MockData.newSession.copy(
                        id = sessionBean.id,
                        studentId = studentBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user updates a session`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val createdSession = sessionService.userCreate(userToken, MockData.newSession.copy(studentId = studentBean.id))
            val updatedSession = sessionService.userUpdate(userToken,
                MockData.newSession.copy(
                    id = createdSession.id,
                    studentId = studentBean.id,
                    status = 3
                )
            )
            assertEquals(3, updatedSession.status)
            assertNotNull(updatedSession)
        }
    }
}