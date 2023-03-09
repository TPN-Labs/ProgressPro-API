package com.progressp.api.config

import com.progressp.config.Measurements
import com.progressp.util.NewRelicTracing
import com.progressp.util.newRelicTrace
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get

fun Route.configApi() {
    route("/config") {
        route("/measurements") {
            newRelicTrace(
                NewRelicTracing("Currency", "All")
            ) {
                get("/all") {
                    val measurements = Measurements.getAll()
                    call.respond(measurements)
                }
            }
        }
    }
}
