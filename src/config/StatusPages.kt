package com.progressp.config

import com.newrelic.api.agent.NewRelic
import com.progressp.util.ApiException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

fun StatusPagesConfig.statusPages() {
    exception<ApiException> { call, cause ->
        logger.error("Code/Exception:[${cause.statusCode.value}][${cause.message}]")
        NewRelic.noticeError(
            cause.javaClass.canonicalName,
            mapOf(
                "message" to cause.message,
                "code" to cause.statusCode.value.toString(),
            ),
            true,
        )
        call.respond(
            cause.statusCode, mapOf(
                "message" to cause.clientMessage,
                "code" to cause.statusCode.value.toString()
            )
        )
    }
    /*exception<Exception> { call, cause ->
        logger.error("Internal exception/ex:${cause}")
        NewRelic.noticeError(cause)
        call.respond(
            HttpStatusCode.InternalServerError, mapOf(
                "message" to "Internal Server Error",
                "code" to "500",
            )
        )
    }*/
}
