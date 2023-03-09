package com.progressp.util

import com.newrelic.api.agent.NewRelic
import com.newrelic.api.agent.Trace
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.request.uri
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.RouteSelectorEvaluation


data class NewRelicTracing(
    val resource: String,
    val method: String,
)

@Trace(dispatcher = true)
fun setNewRelicAttributesMonitoring(
    props: NewRelicTracing,
    request: ApplicationRequest,
) {
    NewRelic.setTransactionName("Enhanced", request.uri)
    NewRelic.addCustomParameter("url-path", request.uri)
    NewRelic.addCustomParameter("device-type", request.headers["Device-Type"])
    NewRelic.addCustomParameter("device-id", request.headers["Device-Id"])
    NewRelic.addCustomParameter("user-agent", request.headers["User-Agent"])
    NewRelic.addCustomParameter("api-resource", props.resource)
    NewRelic.addCustomParameter("api-method", props.method)
    NewRelic.addCustomParameter("api-transaction", "${props.resource}/${props.method}")
}

fun Route.newRelicTrace(props: NewRelicTracing, build: Route.() -> Unit): Route {
    val route = createChild(TransactionSelector())
    val plugin = createRouteScopedPlugin("CustomTracing") {
        on(AuthenticationChecked) { call ->
            setNewRelicAttributesMonitoring(props, call.request)
        }
    }
    route.install(plugin)
    route.build()
    return route
}

private class TransactionSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}
