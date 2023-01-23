package com.progressp.util

import com.progressp.config.EMAIL_ADDRESS_PATTERN
import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.StudentGender
import com.progressp.models.student.StudentsTable
import com.progressp.models.user.PreferenceName
import com.progressp.models.user.UsersTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.slf4j.LoggerFactory
import java.util.UUID

class Preconditions(private val client: IDatabaseFactory) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun checkIfUserExists(userId: String): Boolean {
        logger.debug("Checking if user exists: $userId")
        return client.dbQuery {
            val userInDatabase = UsersTable.select { (UsersTable.id eq UUID.fromString(userId)) }.firstOrNull()
            userInDatabase != null
        }
    }

    fun checkIfEmailIsValid(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    suspend fun checkIfEmailExists(email: String): Boolean {
        return client.dbQuery {
            val userInDatabase = UsersTable.select { (UsersTable.email eq email) }.firstOrNull()
            userInDatabase != null
        }
    }

    fun checkIfPreferenceExists(preferenceName: String): Boolean {
        logger.debug("Checking if preference exists: $preferenceName")
        return PreferenceName.values().any { it.toString() == preferenceName }
    }

    fun checkIfStudentGenderExists(gender: Int): Boolean {
        return StudentGender.values().any { it.code == gender }
    }

    fun checkIfStudentHeightIsValid(height: Double): Boolean {
        return height > 0.0
    }

    suspend fun checkIfStudentExists(studentId: String): Boolean {
        return client.dbQuery {
            val studentInDatabase = StudentsTable.select { (StudentsTable.id eq UUID.fromString(studentId)) }.firstOrNull()
            studentInDatabase != null
        }
    }

    suspend fun checkIfUsernameExists(username: String): Boolean {
        return client.dbQuery {
            val userInDatabase = UsersTable.select { (UsersTable.username eq username.lowercase()) }.firstOrNull()
            userInDatabase != null
        }
    }

    suspend fun checkIfUserCanUpdateStudent(userId: String, studentId: String): Boolean {
        return client.dbQuery {
            val studentInDb = StudentsTable.select {
                (StudentsTable.instructorId eq UUID.fromString(userId)) and
                (StudentsTable.id eq UUID.fromString(studentId))
            }.firstOrNull()
            studentInDb != null
        }
    }
}