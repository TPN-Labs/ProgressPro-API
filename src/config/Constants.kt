package com.progressp.config

import com.auth0.jwt.algorithms.Algorithm
import java.util.regex.Pattern

object APIConstants {
    const val JWT_SECRET: String = "gInaRDBaRyoUNTiSpeRsHBuRecRightnaldIBLEadEbOerEcit"
    const val JWT_ISSUER: String = "progress_pro"
    const val PAGE_LIMIT: Long = 10
    val JWT_ALGORITHM: Algorithm = Algorithm.HMAC256(JWT_SECRET)
}

val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)