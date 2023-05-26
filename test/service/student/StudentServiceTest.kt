package com.progressp.service.student

import com.progressp.data.MockData
import com.progressp.data.MockUUIDs.userList
import com.progressp.database.dbService
import com.progressp.models.student.Student
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import com.progressp.util.StudentMeetingsIsInvalid
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
class StudentServiceTest {

    private lateinit var studentService: StudentService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        studentService = StudentService(dbService)
        userService = UserService(dbService)
        runBlocking {
            dbService.dbQuery {
                StudentsTable.deleteAll()
                UsersTable.deleteAll()
            }
        }
    }

    @Test
    fun `user does not create a student if student meetings is invalid`() {
        runBlocking {
            assertFailsWith(StudentMeetingsIsInvalid::class) {
                val userToken = progressJWT.sign(userList[0], 0, "mock-username-token")
                studentService.userCreate(userToken, MockData.newStudent.copy(totalMeetings = 0))
            }
        }
    }

    @Test
    fun `user creates a student`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val result = studentService.userCreate(userToken, MockData.newStudent)
            assertNotNull(result)
        }
    }

    @Test
    fun `user reads a page of results`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")

            studentService.userCreate(userToken, MockData.newStudent)
            studentService.userCreate(userToken, MockData.newStudent)
            val result = studentService.userAll(userToken)
            assertEquals(2, result.size)
        }
    }

    @Test
    fun `user does not update a student if gender does not exist`() {
        runBlocking {
            assertFailsWith(StudentMeetingsIsInvalid::class) {
                val userToken = progressJWT.sign(userList[0], 0, "mock-username-token")
                studentService.userUpdate(userToken, MockData.newStudent.copy(totalMeetings = 0))
            }
        }
    }

    @Test
    fun `user does not update a student if the user did not create it`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userToken = progressJWT.sign(userList[0], 0, "mock-username-token")
                studentService.userUpdate(userToken, MockData.newStudent)
            }
        }
    }

    @Test
    fun `user updates a student`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")

            val createdStudent = studentService.userCreate(userToken, MockData.newStudent)
            val updatedStudent = studentService.userUpdate(userToken,
                Student.New(
                    id = createdStudent.id,
                    fullName = "OtherName",
                    totalMeetings = 1,
                )
            )
            assertEquals("OtherName", updatedStudent.fullName)
        }
    }

    @Test
    fun `user does not delete a student if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                studentService.userDelete(userToken, Student.Delete(MockData.newStudent.id!!))
            }
        }
    }

    @Test
    fun `user deletes a student`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")

            val createdStudent = studentService.userCreate(userToken, MockData.newStudent)
            studentService.userDelete(userToken, Student.Delete(createdStudent.id))
            dbService.dbQuery {
                val result = Student.findById(UUID.fromString(createdStudent.id))
                assertNull(result)
            }
        }
    }
}
