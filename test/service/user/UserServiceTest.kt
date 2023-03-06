package com.progressp.service.user

import com.progressp.data.MockData.newUser
import com.progressp.database.dbService
import com.progressp.models.user.User
import com.progressp.models.user.UsersTable
import com.progressp.util.UserEmailExists
import com.progressp.util.UserEmailInvalid
import com.progressp.util.UserIncorrectPassword
import com.progressp.util.UsernameExists
import com.progressp.util.getUserDataFromJWT
import com.progressp.util.progressJWT
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        dbService.init()
        userService = UserService(dbService)
        runBlocking {
            dbService.dbQuery {
                UsersTable.deleteAll()
            }
        }
    }

    @Test
    fun `it does not register an user with invalid e-mail`() {
        runBlocking {
            assertFailsWith(UserEmailInvalid::class) {
                userService.userRegister(
                    newUser.copy(email = "invalidmail@")
                )
            }
        }
    }

    @Test
    fun `it does not register an user with existing e-mail`() {
        runBlocking {
            userService.userRegister(newUser)
            assertFailsWith(UserEmailExists::class) {
                userService.userRegister(newUser)
            }
        }
    }

    @Test
    fun `it does not register an user with existing username`() {
        runBlocking {
            userService.userRegister(newUser)
            assertFailsWith(UsernameExists::class) {
                userService.userRegister(newUser.copy(email = "other@mail.com"))
            }
        }
    }

    @Test
    fun `it registers an user`() {
        runBlocking {
            val result = userService.userRegister(newUser)
            assertNotNull(result)
        }
    }

    @Test
    fun `it does not login if password incorrect`() {
        runBlocking {
            userService.userRegister(newUser)
            assertFailsWith(UserIncorrectPassword::class) {
                userService.userLogin(
                    User.Login(user = newUser.email, password = "incorrect"),
                    "device-type",
                    "device-ip",
                    "device-id",
                )
            }
        }
    }

    @Test
    fun `it authenticates user`() {
        runBlocking {
            val originalPass = newUser.copy().password!!
            val result = userService.userRegister(newUser)
            val expectedToken = progressJWT.sign(result.id, 0, result.username)
            val actualToken = userService.userLogin(
                User.Login(user = newUser.username, password = originalPass),
                "device-type",
                "device-ip",
                "device-id",
            )
            val actualIdFromToken = getUserDataFromJWT(actualToken, "id") as String
            val expectedIdFromToken = getUserDataFromJWT(expectedToken, "id") as String
            assertEquals(actualIdFromToken, expectedIdFromToken)
        }
    }

    @Test
    fun `admin updates user`() {
        runBlocking {
            val registeredUser = userService.userRegister(newUser)
            userService.adminUpdate(
                User.UpdateAdmin(
                    id = registeredUser.id,
                    username = "another-username",
                    email = newUser.email,
                    role = 0,
                    premium = 0
                )
            )
            dbService.dbQuery {
                val result = User.findById(UUID.fromString(registeredUser.id))
                assertEquals("another-username", result!!.username)
            }
        }
    }

    @Test
    fun `admin deletes a user`() {
        runBlocking {
            val registeredUser = userService.userRegister(newUser)
            userService.adminDelete(registeredUser.id)
            dbService.dbQuery {
                val result = User.findById(UUID.fromString(registeredUser.id))
                assertNull(result)
            }
        }
    }
}