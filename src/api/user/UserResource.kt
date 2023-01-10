package com.progressp.api.user

import com.progressp.models.user.User
import com.progressp.service.user.IUserService
import com.progressp.util.NewRelicTracing
import com.progressp.util.admin
import com.progressp.util.newRelicTrace
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersApi(userService: IUserService) {
    admin {
        route("/users") {
            newRelicTrace(
                NewRelicTracing("User", "Admin", "All")
            ) {
                get("/page/{page}") {
                    val users = userService.adminRead(call.parameters["page"]!!)
                    call.respond(
                        (mapOf(
                            "items" to users.items,
                            "total" to users.total,
                        ))
                    )
                }
            }
            newRelicTrace(
                NewRelicTracing("User", "Admin", "Update")
            ) {
                put("/") {
                    val user = userService.adminUpdate(call.receive())
                    call.respond(HttpStatusCode.Created, user)
                }
            }
            newRelicTrace(
                NewRelicTracing("User", "Admin", "Delete")
            ) {
                delete("/{id}") {
                    val user = userService.adminDelete(call.parameters["id"]!!)
                    call.respond(HttpStatusCode.OK, user)
                }
            }
        }
    }

    route("/auth") {
        newRelicTrace(
            NewRelicTracing("Auth", "User", "Login")
        ) {
            post("/login") {
                val userDetails = call.receive<User.Login>()
                val deviceType: String = call.request.headers["Device-Type"] ?: "not-set"
                val deviceId: String = call.request.headers["Device-Id"] ?: "not-set"
                val deviceIp: String = call.request.origin.remoteHost
                val response = userService.userLogin(userDetails, deviceType, deviceIp, deviceId)
                call.respond(HttpStatusCode.OK, mapOf("authKey" to response))
            }
        }

        newRelicTrace(
            NewRelicTracing("Auth", "User", "Register")
        ) {
            post("/register") {
                val newUser = call.receive<User.Register>()
                val user = userService.userRegister(newUser)
                call.respond(HttpStatusCode.Created, user)
            }
        }

        newRelicTrace(
            NewRelicTracing("Auth", "User", "Delete")
        ) {
            post("/delete") {
                val token = call.request.authorization()?.removePrefix("Bearer ")!!
                call.respond(userService.userDelete(token))
            }
        }
    }
}