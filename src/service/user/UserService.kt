package com.progressp.service.user

import com.progressp.config.APIConstants
import com.progressp.database.IDatabaseFactory
import com.progressp.database.PaginatedResult
import com.progressp.models.log.AuthLog
import com.progressp.models.user.Preference
import com.progressp.models.user.PreferenceName
import com.progressp.models.user.User
import com.progressp.models.user.UsersTable
import com.progressp.models.user.UsersTable.email
import com.progressp.models.user.UsersTable.username
import com.progressp.service.utils.MetricNames
import com.progressp.service.utils.NewRelicService
import com.progressp.util.Preconditions
import com.progressp.util.UserEmailExists
import com.progressp.util.UserEmailInvalid
import com.progressp.util.UserIncorrectPassword
import com.progressp.util.UserNotFound
import com.progressp.util.UsernameExists
import com.progressp.util.getUserDataFromJWT
import com.progressp.util.progressJWT
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.util.*

interface IUserService {
    suspend fun adminRead(pageId: String): PaginatedResult<User.Page>
    suspend fun adminUpdate(newUser: User.UpdateAdmin): User.Response
    suspend fun adminDelete(userId: String): User.Response

    suspend fun userRegister(newUser: User.Register): User.Response
    suspend fun userLogin(
        newUser: User.Login,
        headerDeviceType: String,
        headerDeviceIp: String,
        headerDeviceId: String
    ): String

    suspend fun userDelete(token: String): User.Response
}

class UserService(private val databaseFactory: IDatabaseFactory) : IUserService {
    private val _newRelicService = NewRelicService()

    private suspend fun getByEmailOrUsername(user: String): Map<String, String> {
        return databaseFactory.dbQuery {
            val userRow = UsersTable.select {
                (username eq user) or (email eq user)
            }.firstOrNull() ?: throw UserNotFound(user)
            mapOf(
                "id" to userRow[UsersTable.id].toString(),
                "username" to userRow[username].toString(),
                "role" to userRow[UsersTable.role].toString(),
                "password" to userRow[UsersTable.password].toString()
            )
        }
    }

    override suspend fun userRegister(newUser: User.Register): User.Response {
        if (!Preconditions(databaseFactory).checkIfEmailIsValid(newUser.email)) throw UserEmailInvalid(newUser.email)
        if (Preconditions(databaseFactory).checkIfEmailExists(newUser.email)) throw UserEmailExists(newUser.email)
        if (Preconditions(databaseFactory).checkIfUsernameExists(newUser.username)) throw UsernameExists(newUser.username)

        val totalUsers = databaseFactory.dbQuery { User.all().count() }
        _newRelicService.recordMetric(MetricNames.USERS_REGISTERED, totalUsers.toFloat())

        return databaseFactory.dbQuery {
            val userRow = UsersTable.insert {
                it[email] = newUser.email
                it[password] = BCrypt.hashpw(newUser.password, BCrypt.gensalt(10))
                it[username] = newUser.username.lowercase()
                it[role] = 0
                it[premium] = 0
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            Preference.new {
                userId = User.findById(UUID.fromString(userRow[UsersTable.id].toString()))!!.id
                optionValue = newUser.username.lowercase()
                optionName = PreferenceName.DISPLAY_NAME.name
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            User.Response(
                id = userRow[UsersTable.id].toString(),
                username = userRow[username].toString()
            )
        }
    }

    override suspend fun userLogin(
        newUser: User.Login,
        headerDeviceType: String,
        headerDeviceIp: String,
        headerDeviceId: String,
    ): String {
        val userRow = getByEmailOrUsername(newUser.user.lowercase())
        if (!BCrypt.checkpw(newUser.password, userRow["password"]))
            throw UserIncorrectPassword()

        databaseFactory.dbQuery {
            AuthLog.new {
                userId = User.findById(UUID.fromString(userRow["id"]))!!.id
                ip = headerDeviceIp
                method = "FORM"
                deviceId = headerDeviceId
                device = headerDeviceType
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
        }

        _newRelicService.incrementMetric(MetricNames.USERS_LOGGED)
        return progressJWT.sign(
            userRow["id"].toString(),
            Integer.parseInt(userRow["role"]),
            newUser.user,
        )
    }

    override suspend fun userDelete(token: String): User.Response {
        val userId = getUserDataFromJWT(token, "id") as String
        if (!Preconditions(databaseFactory).checkIfUserExists(userId)) throw UserNotFound(userId)
        _newRelicService.incrementMetric(MetricNames.USERS_DELETED)

        return databaseFactory.dbQuery {
            UsersTable.deleteWhere { (UsersTable.id eq UUID.fromString(userId)) }
            User.Response(
                id = userId,
                username = "deleted"
            )
        }
    }

    override suspend fun adminRead(pageId: String): PaginatedResult<User.Page> {
        val page = Integer.parseInt(pageId)
        return databaseFactory.dbQuery {
            val users = User.all().orderBy(UsersTable.createdAt to SortOrder.DESC)
                .limit(APIConstants.PAGE_LIMIT.toInt(), page * APIConstants.PAGE_LIMIT)
            val total = User.all().count()
            val list = users.map { User.Page.fromUserRow(it) }
            PaginatedResult(list, total.toInt())
        }
    }

    override suspend fun adminUpdate(newUser: User.UpdateAdmin): User.Response {
        if (!Preconditions(databaseFactory).checkIfUserExists(newUser.id)) throw UserNotFound(newUser.id)

        return databaseFactory.dbQuery {
            UsersTable.select { (UsersTable.id eq UUID.fromString(newUser.id)) }.firstOrNull()
            UsersTable.update({ UsersTable.id eq UUID.fromString(newUser.id) }) {
                it[email] = newUser.email
                it[username] = newUser.username
                it[role] = newUser.role
                it[premium] = newUser.premium
                it[updatedAt] = LocalDateTime.now()
            }
            val updatedUser = UsersTable.select { (UsersTable.id eq UUID.fromString(newUser.id)) }.first()
            User.Response(
                id = updatedUser[UsersTable.id].toString(),
                username = updatedUser[username].toString()
            )
        }
    }

    override suspend fun adminDelete(userId: String): User.Response {
        if (!Preconditions(databaseFactory).checkIfUserExists(userId)) throw UserNotFound(userId)
        return databaseFactory.dbQuery {
            UsersTable.deleteWhere { (UsersTable.id eq UUID.fromString(userId)) }
            User.Response(
                id = userId,
                username = "deleted"
            )
        }
    }
}