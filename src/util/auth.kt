package com.progressp.util

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.progressp.config.APIConstants.JWT_ALGORITHM
import com.progressp.config.APIConstants.JWT_ISSUER
import com.progressp.database.IDatabaseFactory
import com.progressp.models.user.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*

fun getUserDataFromJWT(token: String, claim: String): Any {
    try {
        val dataJwt: DecodedJWT = JWT.require(JWT_ALGORITHM)
            .withIssuer(JWT_ISSUER)
            .build().verify(token)
        when (claim) {
            "id" -> return dataJwt.getClaim("id").asString()
            "username" -> return dataJwt.getClaim("username").asString()
            "role" -> return dataJwt.getClaim("role").asInt()
        }
    } catch (ex: SignatureVerificationException) {
        throw JwtSignatureFails(ex.toString())
    } catch (ex: TokenExpiredException) {
        throw JwtSignatureFails(ex.toString())
    }
    return "not-found"
}

fun isAdmin(token: String): Boolean {
    return getUserDataFromJWT(token, "role") == 1339
}

suspend fun isEmailValidated(token: String, client: IDatabaseFactory): Boolean {
    val userId = getUserDataFromJWT(token, "id")
    return client.dbQuery {
        val user = User.findById(UUID.fromString(userId as String))
        true
    }
}

fun Route.admin(build: Route.() -> Unit): Route {
    val route = createChild(CustomSelector())
    val plugin = createRouteScopedPlugin("CustomAuthorization") {
        on(AuthenticationChecked) { call ->
            if (call.request.authorization() == null) {
                throw UserNotAuthenticated()
            }
            val token = call.request.authorization()?.removePrefix("Bearer ")!!
            if (!isAdmin(token)) {
                val userId = getUserDataFromJWT(token, "id")
                throw UserNotAdmin(userId as String)
            }
        }
    }
    route.install(plugin)
    route.build()
    return route
}

fun Route.userValidated(client: IDatabaseFactory, build: Route.() -> Unit): Route {
    val route = createChild(CustomSelector())
    val plugin = createRouteScopedPlugin("CustomAuthorization") {
        on(AuthenticationChecked) { call ->
            if (call.request.authorization() == null) {
                throw UserNotAuthenticated()
            }
            val token = call.request.authorization()?.removePrefix("Bearer ")!!
            if (!isEmailValidated(token, client)) {
                val userId = getUserDataFromJWT(token, "id")
                throw UserEmailNotVerified(userId as String)
            }
        }
    }
    route.install(plugin)
    route.build()
    return route
}

private class CustomSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}