package com.progressp.service.student

import com.progressp.config.MeasurementCode
import com.progressp.data.MockData
import com.progressp.data.MockUUIDs
import com.progressp.database.dbService
import com.progressp.models.student.StudentsNotesTable
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import com.progressp.util.MeasurementCodeNotFound
import com.progressp.util.StudentNotFound
import com.progressp.util.StudentNotYours
import com.progressp.util.StudentNoteNotFound
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
class StudentNoteServiceTest {

    private lateinit var studentService: StudentService
    private lateinit var noteService: StudentNoteService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        studentService = StudentService(dbService)
        noteService = StudentNoteService(dbService)
        userService = UserService(dbService)
        runBlocking {
            dbService.dbQuery {
                StudentsTable.deleteAll()
                StudentsNotesTable.deleteAll()
                UsersTable.deleteAll()
            }
        }
    }

    @Test
    fun `user does not create a note if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                noteService.userCreate(userToken, MockData.newNote)
            }
        }
    }

    @Test
    fun `user does not create a note if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                noteService.userCreate(
                    otherToken, MockData.newNote.copy(studentId = studentBean.id)
                )
            }
        }
    }

    @Test
    fun `user does not create a note if measurement does not exist`() {
        runBlocking {
            assertFailsWith(MeasurementCodeNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                noteService.userCreate(
                    userToken, MockData.newNote.copy(studentId = studentBean.id, measurementName = "something")
                )
            }
        }
    }

    @Test
    fun `user creates a note`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val result = noteService.userCreate(
                userToken, MockData.newNote.copy(studentId = studentBean.id)
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

            noteService.userCreate(
                userToken, MockData.newNote.copy(studentId = studentBean.id)
            )
            noteService.userCreate(
                userToken, MockData.newNote.copy(studentId = studentBean.id)
            )

            val result = noteService.userAll(userToken)
            assertEquals(2, result.size)
        }
    }

    @Test
    fun `user does not update a note if student does not exist`() {
        runBlocking {
            assertFailsWith(StudentNotFound::class) {
                val userToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token")
                noteService.userUpdate(userToken, MockData.newNote)
            }
        }
    }

    @Test
    fun `user does not update a note if student is not theirs`() {
        runBlocking {
            assertFailsWith(StudentNotYours::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                val otherToken = progressJWT.sign(MockUUIDs.userList[0], 0, "mock-username-token-2")
                noteService.userUpdate(
                    otherToken, MockData.newNote.copy(studentId = studentBean.id)
                )
            }
        }
    }

    @Test
    fun `user does not update a note if measurement does not exist`() {
        runBlocking {
            assertFailsWith(MeasurementCodeNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)
                noteService.userUpdate(
                    userToken, MockData.newNote.copy(studentId = studentBean.id, measurementName = "something")
                )
            }
        }
    }

    @Test
    fun `user does not update a note if note does not exist`() {
        runBlocking {
            assertFailsWith(StudentNoteNotFound::class) {
                val userBean = userService.userRegister(MockData.newUser)
                val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
                val studentBean = studentService.userCreate(userToken, MockData.newStudent)

                noteService.userCreate(
                    userToken, MockData.newNote.copy(studentId = studentBean.id)
                )

                noteService.userUpdate(
                    userToken,
                    MockData.newNote.copy(
                        studentId = studentBean.id,
                    )
                )
            }
        }
    }

    @Test
    fun `user updates a note`() {
        runBlocking {
            val userBean = userService.userRegister(MockData.newUser)
            val userToken = progressJWT.sign(userBean.id, 0, "mock-username-token")
            val studentBean = studentService.userCreate(userToken, MockData.newStudent)
            val createdNote = noteService.userCreate(
                userToken, MockData.newNote.copy(studentId = studentBean.id)
            )
            val updatedNote = noteService.userUpdate(
                userToken,
                MockData.newNote.copy(
                    id = createdNote.id,
                    studentId = studentBean.id,
                    measurementName = MeasurementCode.FEMUR.toString().lowercase(),
                )
            )
            assertNotNull(updatedNote)
            assertEquals(updatedNote.measurementName, MeasurementCode.FEMUR.toString().lowercase())
        }
    }
}
