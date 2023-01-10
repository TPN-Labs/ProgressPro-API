package com.progressp.models.user

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object PreferencesTable : UUIDTable("users_preferences") {
    val userId: Column<EntityID<UUID>> = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val optionName: Column<String> = varchar("option_name", 64)
    val optionValue: Column<String> = varchar("option_value", 256)
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class Preference(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Preference>(PreferencesTable)

    var userId by PreferencesTable.userId
    var optionName by PreferencesTable.optionName
    var optionValue by PreferencesTable.optionValue
    var updatedAt by PreferencesTable.updatedAt
    var createdAt by PreferencesTable.createdAt

    data class PreferenceNew(
        val id: String?,
        val optionName: String,
        val optionValue: String,
    )

    data class Response(
        val id: String,
        val optionName: String,
        val optionValue: String,
    ) {
        companion object {
            fun fromRow(row: Preference): Response = Response(
                id = row.id.toString(),
                optionName = row.optionName,
                optionValue = row.optionValue,
            )
        }
    }
}

enum class PreferenceName {
    SHOW_ADS,
    NOTIFICATION_STATISTICS,
    FCM_TOKEN,
    DISPLAY_NAME,
}

enum class PreferenceValue(val optionValue: String) {
    ENABLED("1"),
    DISABLED("0"),
}