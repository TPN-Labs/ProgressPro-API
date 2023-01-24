package com.progressp

import com.progressp.config.api
import com.progressp.api.index
import com.progressp.config.statusPages
import com.progressp.config.configureCORS
import com.progressp.database.IDatabaseFactory

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.progressp.util.progressJWT
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.sentry.Sentry
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main.kt")
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) { statusPages() }
    install(CORS) { configureCORS() }
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
            registerModule(JavaTimeModule())
        }
    }
    install(Koin) {
        modules(databaseKoinModule)
        modules(serviceKoinModule)
    }
    install(Authentication) {
        jwt {
            // Configure jwt authentication
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            verifier(progressJWT.verifier)
        }
    }

    if(System.getenv("SENTRY_DSN") != null) {
        Sentry.init { options ->
            options.dsn = System.getenv("SENTRY_DSN")
            options.tracesSampleRate = 0.5
        }
    }

    val factory: IDatabaseFactory by inject()
    factory.init()

    install(Routing) {
        index()
        api()
    }

    logger.info("Server started successfully!")
}

fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)