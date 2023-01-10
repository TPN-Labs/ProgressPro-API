package com.progressp.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
