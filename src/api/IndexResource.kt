package com.progressp.api

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.index() {

    val indexPage = javaClass.getResource("/index.html")?.readText()

    get("/") {
        call.respondText(indexPage!!, ContentType.Text.Html)
    }

    get("/ping") {
        call.respond("Pong")
    }

    get("/health-check") {
        call.respond("OK")
    }
}
