package com.progressp.util

import com.progressp.config.EMAIL_ADDRESS_PATTERN
import com.progressp.database.IDatabaseFactory
import com.progressp.models.user.PreferenceName
import com.progressp.models.user.UsersTable
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

    suspend fun checkIfUsernameExists(username: String): Boolean {
        return client.dbQuery {
            val userInDatabase = UsersTable.select { (UsersTable.username eq username.lowercase()) }.firstOrNull()
            userInDatabase != null
        }
    }
}