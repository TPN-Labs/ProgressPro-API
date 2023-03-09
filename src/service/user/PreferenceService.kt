package com.progressp.service.user

import com.progressp.database.IDatabaseFactory
import com.progressp.models.user.Preference
import com.progressp.models.user.PreferenceName
import com.progressp.models.user.PreferencesTable
import com.progressp.models.user.User
import com.progressp.util.Preconditions
import com.progressp.util.PreferenceDoesNotExist
import com.progressp.util.PreferenceNotFound
import com.progressp.util.getUserDataFromJWT
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.UUID

interface IPreferenceService {
    suspend fun userAll(token: String): ArrayList<Preference.Response>
    suspend fun userRead(token: String, preferenceName: PreferenceName): Preference.Response
    suspend fun userCreate(token: String, preferenceProps: Preference.PreferenceNew): Preference.Response
    suspend fun userUpdate(token: String, preferenceProps: Preference.PreferenceNew): Preference.Response
}

class PreferenceService(private val databaseFactory: IDatabaseFactory) : IPreferenceService {

    private fun getPreference(id: String) = Preference.findById(UUID.fromString(id)) ?: throw PreferenceNotFound(id)

    override suspend fun userAll(token: String): ArrayList<Preference.Response> {
        val userId = getUserDataFromJWT(token, "id") as String
        val list = ArrayList<Preference.Response>()
        return databaseFactory.dbQuery {
            Preference.find { PreferencesTable.userId eq User.findById(UUID.fromString(userId))!!.id }
                .orderBy(PreferencesTable.createdAt to SortOrder.DESC).forEach {
                    list.add(
                        Preference.Response.fromRow(it)
                    )
                }
            list
        }
    }

    override suspend fun userRead(token: String, preferenceName: PreferenceName): Preference.Response {
        val userId = getUserDataFromJWT(token, "id") as String
        return databaseFactory.dbQuery {
            val preference = Preference.find {
                (PreferencesTable.userId eq User.findById(UUID.fromString(userId))!!.id) and
                (PreferencesTable.optionName eq preferenceName.name)
            }.firstOrNull() ?: throw PreferenceDoesNotExist(preferenceName.name, userId)
            Preference.Response.fromRow(preference)
        }
    }

    override suspend fun userCreate(
        token: String,
        preferenceProps: Preference.PreferenceNew
    ): Preference.Response {
        val paramUserId = getUserDataFromJWT(token, "id") as String
        val preferenceName = preferenceProps.optionName.uppercase()
        if(!Preconditions(databaseFactory).checkIfPreferenceExists(preferenceName))
            throw PreferenceDoesNotExist(preferenceName, paramUserId)

        return databaseFactory.dbQuery {
            val preference = Preference.new {
                userId = User.findById(UUID.fromString(paramUserId))!!.id
                optionName = preferenceName
                optionValue = preferenceProps.optionValue
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            Preference.Response.fromRow(preference)
        }
    }

    override suspend fun userUpdate(
        token: String,
        preferenceProps: Preference.PreferenceNew
    ): Preference.Response {
        val paramUserId = getUserDataFromJWT(token, "id") as String
        val preferenceName = preferenceProps.optionName.uppercase()
        if(!Preconditions(databaseFactory).checkIfPreferenceExists(preferenceName))
            throw PreferenceDoesNotExist(preferenceName, paramUserId)

        return databaseFactory.dbQuery {
            val preference = getPreference(preferenceProps.id!!)
            preference.apply {
                optionValue = preferenceProps.optionValue
                updatedAt = LocalDateTime.now()
            }
            Preference.Response.fromRow(preference)
        }
    }
}
