package com.progressp.api.user

import com.progressp.models.user.User
import com.progressp.service.user.IUserService
import com.progressp.util.NewRelicTracing
import com.progressp.util.admin
import com.progressp.util.newRelicTrace
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.request.authorization
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.usersApi(userService: IUserService) {
    admin {
        route("/users") {
            newRelicTrace(
                NewRelicTracing("User", "All")
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
                NewRelicTracing("User", "Update")
            ) {
                put("/") {
                    val user = userService.adminUpdate(call.receive())
                    call.respond(HttpStatusCode.Created, user)
                }
            }
            newRelicTrace(
                NewRelicTracing("User", "Delete")
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
            NewRelicTracing("Auth", "Login")
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
            NewRelicTracing("Auth", "Register")
        ) {
            post("/register") {
                val newUser = call.receive<User.Register>()
                val user = userService.userRegister(newUser)
                call.respond(HttpStatusCode.Created, user)
            }
        }

        newRelicTrace(
            NewRelicTracing("Auth", "Delete")
        ) {
            post("/delete") {
                val token = call.request.authorization()?.removePrefix("Bearer ")!!
                call.respond(HttpStatusCode.OK, userService.userDelete(token))
            }
        }
    }
}
