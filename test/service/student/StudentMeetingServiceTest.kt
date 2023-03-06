package com.progressp.service.student

import com.progressp.database.dbService
import com.progressp.models.student.StudentsMeetingsTable
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.UsersTable
import com.progressp.service.user.UserService
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.util.*

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

}