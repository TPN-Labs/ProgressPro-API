package com.progressp.service.student

import com.progressp.data.MockData
import com.progressp.data.MockUUIDs
import com.progressp.database.dbService
import com.progressp.models.student.StudentMeeting
import com.progressp.models.student.StudentsMeetingsTable
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import com.progressp.util.StudentMeetingNotFound
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
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
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentMeetingServiceTest {

    private lateinit var studentService: StudentService
    private lateinit var meetingService: StudentMeetingService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        studentService = StudentService(dbService)
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
    fun `user does not create a meeting if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")

                meetingService.userCreate(
                    userToken, MockData.newMeeting
                )
            }
        }
    }

    @Test
    fun `user does not create a meeting if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mockUsernameToken-2")
                meetingService.userCreate(
                    otherToken, MockData.newMeeting.copy(
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
            val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val result = meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
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
            val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)

            meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    studentId = studentBean.id
                )
            )
            meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    studentId = studentBean.id
                )
            )
            val result = meetingService.userAll(userToken)

            assertEquals(2, result.size)
        }
    }

    @Test
    fun `user does not update a meeting if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")

                meetingService.userUpdate(
                    userToken, MockData.newMeeting
                )
            }
        }
    }

    @Test
    fun `user does not update a meeting if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mockUsernameToken-2")
                meetingService.userUpdate(
                    otherToken, MockData.newMeeting.copy(
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
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                meetingService.userUpdate(
                    userToken,
                    MockData.newMeeting.copy(
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
            val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val createdMeeting = meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    studentId = studentBean.id
                )
            )
            val updatedMeeting = meetingService.userUpdate(
                userToken,
                MockData.newMeeting.copy(
                    id = createdMeeting.id,
                    studentId = studentBean.id,
                    startAt = "2000-01-01T11:00:00"
                )
            )
            assertNotNull(updatedMeeting)
        }
    }

    @Test
    fun `user does not delete a meeting if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mockUsernameToken-2")
                val createdMeeting = meetingService.userCreate(
                    userToken, MockData.newMeeting.copy(
                        studentId = studentBean.id
                    )
                )
                meetingService.userDelete(
                    otherToken,
                    StudentMeeting.Delete(
                        createdMeeting.id,
                        studentBean.id
                    )
                )
            }
        }
    }

    @Test
    fun `user deletes a meeting`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mockUsernameToken")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val createdMeeting = meetingService.userCreate(
                userToken, MockData.newMeeting.copy(
                    studentId = studentBean.id
                )
            )
            meetingService.userDelete(
                userToken,
                StudentMeeting.Delete(
                    createdMeeting.id,
                    studentBean.id
                )
            )
            dbService.dbQuery {
                val result = StudentMeeting.findById(UUID.fromString(createdMeeting.id))
                assertNull(result)
            }
        }
    }
}
