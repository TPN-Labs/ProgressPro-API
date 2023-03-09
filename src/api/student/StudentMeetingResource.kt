package com.progressp.api.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.StudentMeeting
import com.progressp.service.student.IStudentMeetingService
import com.progressp.util.NewRelicTracing
import com.progressp.util.newRelicTrace
import com.progressp.util.userValidated
import io.ktor.server.application.call
import io.ktor.server.request.authorization
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.studentsMeetingsApi(studentMeetingService: IStudentMeetingService, databaseFactory: IDatabaseFactory) {

    userValidated(databaseFactory) {
        route("/students_meetings") {
            newRelicTrace(
                NewRelicTracing("StudentMeeting", "All")
            ) {
                get("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferences = studentMeetingService.userAll(token)
                    call.respond(preferences)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentMeeting", "Create")
            ) {
                post("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodyMeeting = call.receive<StudentMeeting.New>()
                    val student = studentMeetingService.userCreate(token, bodyMeeting)
                    call.respond(student)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentMeeting", "Update")
            ) {
                put("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodyMeeting = call.receive<StudentMeeting.New>()
                    val student = studentMeetingService.userUpdate(token, bodyMeeting)
                    call.respond(student)
                }
            }
        }
    }
}
