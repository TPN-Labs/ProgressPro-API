package com.progressp.config

import com.auth0.jwt.algorithms.Algorithm
import java.util.regex.Pattern

object APIConstants {
    const val JWT_SECRET: String = "gInaRDBaRyoUNTiSpeRsHBuRecRightnaldIBLEadEbOerEcit"
    const val JWT_ISSUER: String = "progress_pro"
    const val PAGE_LIMIT: Long = 10
    const val TOTAL_AVATARS: Int = 14
    const val ADMIN_ROLE: Int = 1339
    const val SENTRY_THRESHOLD: Double = 0.5
    const val BCRYPT_ROUNDS: Int = 10
    val JWT_ALGORITHM: Algorithm = Algorithm.HMAC256(JWT_SECRET)
}

object DbContstants {
    const val MAX_POOL_SIZE = 5
    const val CONN_TIMEOUT = 2000L
    const val LEAK_THRESHOLD = 1000
    const val LEAK_TIMES = 3
    const val STRING_LENGTH = 64
    const val MEDIUM_STRING_LENGTH = 128
    const val LARGE_STRING_LENGTH = 256
}

val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)
