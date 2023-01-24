package com.progressp.api.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.Student
import com.progressp.models.student.StudentSession
import com.progressp.service.student.IStudentSessionService
import com.progressp.util.NewRelicTracing
import com.progressp.util.newRelicTrace
import com.progressp.util.userValidated
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.studentsSessionsApi(studentSessionService: IStudentSessionService, databaseFactory: IDatabaseFactory) {

    userValidated(databaseFactory) {
        route("/students_sessions") {
            newRelicTrace(
                NewRelicTracing("StudentSession", "All")
            ) {
                get("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferences = studentSessionService.userAll(token)
                    call.respond(preferences)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentSession", "Create")
            ) {
                post("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodySession = call.receive<StudentSession.New>()
                    val student = studentSessionService.userCreate(token, bodySession)
                    call.respond(student)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentSession", "Update")
            ) {
                put("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodySession = call.receive<StudentSession.New>()
                    val student = studentSessionService.userUpdate(token, bodySession)
                    call.respond(student)
                }
            }
        }
    }
}