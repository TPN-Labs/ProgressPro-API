package com.progressp.database

val dbService: DatabaseFactory = DatabaseFactory(
    DB_HOST = "127.0.0.1",
    DB_PORT = "5432",
    DB_NAME = "local_ledger_tests",
    DB_USER = "postgres",
    DB_PASS = ""
)
