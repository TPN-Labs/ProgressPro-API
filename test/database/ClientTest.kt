package com.progressp.database

val dbService: DatabaseFactory = DatabaseFactory(
    dbHost = "127.0.0.1",
    dbPort = "5432",
    dbName = "local_progresspro_tests",
    dbUser = "postgres",
    dbPass = "postgres_test"
)
