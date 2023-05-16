package com.progressp.config

import com.progressp.api.config.configApi
import com.progressp.api.student.studentsApi
import com.progressp.api.student.studentsMeetingsApi
import com.progressp.api.student.studentsNoteApi
import com.progressp.api.user.preferenceApi
import com.progressp.api.user.usersApi
import com.progressp.database.IDatabaseFactory
import com.progressp.service.student.IStudentMeetingService
import com.progressp.service.student.IStudentNoteService
import com.progressp.service.student.IStudentService
import com.progressp.service.user.IPreferenceService
import com.progressp.service.user.IUserService
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.api() {

    val dbService: IDatabaseFactory by inject()

    val preferenceService: IPreferenceService by inject()
    val studentService: IStudentService by inject()
    val studentMeetingService: IStudentMeetingService by inject()
    val studentNoteService: IStudentNoteService by inject()
    val userService: IUserService by inject()

    route("/api") {
        configApi()
        usersApi(userService)
        studentsApi(studentService, dbService)
        studentsMeetingsApi(studentMeetingService, dbService)
        studentsNoteApi(studentNoteService, dbService)
        preferenceApi(preferenceService, dbService)
    }
}
