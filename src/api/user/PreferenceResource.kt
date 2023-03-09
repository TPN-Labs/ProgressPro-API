package com.progressp.api.user

import com.progressp.database.IDatabaseFactory
import com.progressp.models.user.Preference
import com.progressp.models.user.PreferenceName
import com.progressp.service.user.IPreferenceService
import com.progressp.util.NewRelicTracing
import com.progressp.util.newRelicTrace
import com.progressp.util.userValidated
import io.ktor.server.application.call
import io.ktor.server.request.authorization
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.preferenceApi(preferenceService: IPreferenceService, databaseFactory: IDatabaseFactory) {

    userValidated(databaseFactory) {
        route("/preferences") {
            newRelicTrace(
                NewRelicTracing("Preference", "All")
            ) {
                get("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferences = preferenceService.userAll(token)
                    call.respond(preferences)
                }
            }

            newRelicTrace(
                NewRelicTracing("Preference", "Create")
            ) {
                post("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val newPreference = call.receive<Preference.PreferenceNew>()
                    val preference = preferenceService.userCreate(token, newPreference)
                    call.respond(preference)
                }
            }

            newRelicTrace(
                NewRelicTracing("Preference", "Update")
            ) {
                put("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val newPreference = call.receive<Preference.PreferenceNew>()
                    val preference = preferenceService.userUpdate(token, newPreference)
                    call.respond(preference)
                }
            }

            newRelicTrace(
                NewRelicTracing("Preference", "Get")
            ) {
                get("/my/{name}") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferenceName = PreferenceName.valueOf(call.parameters["name"]!!.uppercase())
                    val preference = preferenceService.userRead(token, preferenceName)
                    call.respond(preference)
                }
            }
        }
    }
}
