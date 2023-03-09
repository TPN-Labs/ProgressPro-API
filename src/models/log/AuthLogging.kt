package com.progressp.models.log

import com.progressp.config.DbContstants.LARGE_STRING_LENGTH
import com.progressp.config.DbContstants.STRING_LENGTH
import com.progressp.models.user.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object AuthLogsTable : UUIDTable("log_logins") {
    val userId: Column<EntityID<UUID>> = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val ip: Column<String> = varchar("ip", STRING_LENGTH)
    val device: Column<String> = varchar("device", STRING_LENGTH)
    val deviceId: Column<String> = varchar("device_id", LARGE_STRING_LENGTH)
    val method: Column<String> = varchar("method", STRING_LENGTH)
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
}

class AuthLog(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AuthLog>(AuthLogsTable)

    var userId by AuthLogsTable.userId
    var ip by AuthLogsTable.ip
    var method by AuthLogsTable.method
    var device by AuthLogsTable.device
    var deviceId by AuthLogsTable.deviceId
    var updatedAt by AuthLogsTable.updatedAt
    var createdAt by AuthLogsTable.createdAt
}
