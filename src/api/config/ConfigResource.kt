package com.progressp.api.config

import com.progressp.config.Currencies
import com.progressp.config.CurrencyCode
import com.progressp.util.NewRelicTracing
import com.progressp.util.newRelicTrace
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configApi() {
    route("/config") {
        route("/currencies") {
            newRelicTrace(
                NewRelicTracing("Currency", "All", "All")
            ) {
                get("/all") {
                    val currencies = Currencies.getAlphabeticalCurrencies()
                    call.respond(currencies)
                }
            }

            newRelicTrace(
                NewRelicTracing("Currency", "All", "GetCurrencyByCode")
            ) {
                get("/code/{code}") {
                    val currency = Currencies.getCurrencyByCode(call.parameters["code"]!!)
                    call.respond(currency)
                }
            }
        }
    }
}