package com.progressp.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.progressp.config.APIConstants
import java.util.*

private fun dateInFutureMinutes(): Date {
    val minuteToMillis: Long = 60L * 1000

    val totalMinutes = 60 * 24 * 60 // Two months
    val result = Date()
    result.time = result.time + totalMinutes * minuteToMillis
    return result
}

val progressJWT = ProgressJWT()

class ProgressJWT {
    private val algorithm = Algorithm.HMAC256(APIConstants.JWT_SECRET)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(APIConstants.JWT_ISSUER)
        .build()

    fun sign(id: String, role: Int, username: String): String = JWT.create()
        .withIssuer(APIConstants.JWT_ISSUER)
        .withClaim("id", id)
        .withClaim("username", username)
        .withClaim("role", role)
        .withExpiresAt(dateInFutureMinutes())
        .sign(algorithm)
}