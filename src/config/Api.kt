package com.progressp.config

import com.progressp.api.config.configApi
import com.progressp.api.student.studentsApi
import com.progressp.api.user.preferenceApi
import com.progressp.api.user.usersApi
import com.progressp.database.IDatabaseFactory
import com.progressp.service.student.IStudentService
import com.progressp.service.user.IPreferenceService
import com.progressp.service.user.IUserService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.api() {

    val dbService: IDatabaseFactory by inject()

    val preferenceService: IPreferenceService by inject()
    val studentService: IStudentService by inject()
    val userService: IUserService by inject()

    route("/api") {
        configApi()
        usersApi(userService)
        studentsApi(studentService, dbService)
        preferenceApi(preferenceService, dbService)
    }
}