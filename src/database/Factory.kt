package com.progressp.database

import com.progressp.models.log.AuthLogsTable
import com.progressp.models.user.PreferencesTable
import com.progressp.models.user.UsersTable
import com.newrelic.api.agent.Trace
import com.progressp.models.student.StudentsMeetingsTable
import com.progressp.models.student.StudentsNotesTable
import com.progressp.models.student.StudentsSessionsTable
import com.progressp.models.student.StudentsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

data class PaginatedResult<T>(
    val items: List<T>, val total: Int
)

interface IDatabaseFactory {
    fun init()
    suspend fun <T> dbQuery(block: () -> T): T
}

class DatabaseFactory(
    DB_HOST: String = "127.0.0.1",
    DB_PORT: String = "5432",
    DB_USER: String = "postgres",
    DB_PASS: String = "",
    DB_NAME: String = "local_progresspro",
) : IDatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val dbHost: String = System.getenv("DB_HOST") ?: DB_HOST
    private val dbPort: String = System.getenv("DB_PORT") ?: DB_PORT
    private val dbUser: String = System.getenv("DB_USER") ?: DB_USER
    private val dbPass: String = System.getenv("DB_PASS") ?: DB_PASS
    private val dbName: String = System.getenv("DB_NAME") ?: DB_NAME

    private fun hikari(): HikariDataSource {
        val envJdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        logger.info("[DB] Connecting to $envJdbcUrl")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = envJdbcUrl
            username = dbUser
            password = dbPass
            maximumPoolSize = 5
            minimumIdle = 1
            connectionTimeout = 2000
            leakDetectionThreshold = (3 * 1000).toLong()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        return HikariDataSource(config)
    }

    @Trace(dispatcher = true)
    override fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(
                AuthLogsTable,
                PreferencesTable,
                StudentsTable,
                StudentsMeetingsTable,
                StudentsNotesTable,
                StudentsSessionsTable,
                UsersTable,
            )
        }
    }

    @Trace(dispatcher = true)
    override suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }
}