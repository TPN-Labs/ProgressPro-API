package com.progressp.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.progressp.config.APIConstants
import java.util.*

private const val TOTAL_DAYS = 60
private const val SEC_IN_MIN = 60L
private const val MIN_IN_HOUR = 60
private const val HOUR_IN_DAY = 24
private const val MILLIS_IN_SEC = 1000

private fun dateInFutureMinutes(): Date {
    val minuteToMillis: Long = SEC_IN_MIN * MILLIS_IN_SEC

    val totalMinutes = MIN_IN_HOUR * HOUR_IN_DAY * TOTAL_DAYS  // Two months
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
