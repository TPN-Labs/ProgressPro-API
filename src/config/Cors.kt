package com.progressp.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.plugins.cors.CORSConfig

fun CORSConfig.configureCORS() {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.AccessControlAllowHeaders)
    allowHeader(HttpHeaders.AccessControlAllowOrigin)
    allowHeader(HttpHeaders.AccessControlRequestHeaders)
    allowHeader(HttpHeaders.AccessControlRequestMethod)
    allowHeader(HttpHeaders.AcceptEncoding)
    allowHeader(HttpHeaders.AcceptLanguage)
    allowHeader(HttpHeaders.Connection)
    allowHeader(HttpHeaders.Host)
    allowHeader(HttpHeaders.Accept)
    allowHeader(HttpHeaders.Authorization)
    allowHeader("Device-Type")
    allowHeader("Device-Id")
    allowNonSimpleContentTypes = true
    allowCredentials = true
    allowSameOrigin = true
    anyHost()
    // maxAgeDuration = Duration.days(1)
}
