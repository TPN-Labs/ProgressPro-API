package com.progressp.api.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.StudentNote
import com.progressp.service.student.IStudentNoteService
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

fun Route.studentsNoteApi(studentNoteService: IStudentNoteService, databaseFactory: IDatabaseFactory) {

    userValidated(databaseFactory) {
        route("/students_notes") {
            newRelicTrace(
                NewRelicTracing("StudentNote", "All")
            ) {
                get("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferences = studentNoteService.userAll(token)
                    call.respond(preferences)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentNote", "Create")
            ) {
                post("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodySession = call.receive<StudentNote.New>()
                    val student = studentNoteService.userCreate(token, bodySession)
                    call.respond(student)
                }
            }

            newRelicTrace(
                NewRelicTracing("StudentNote", "Update")
            ) {
                put("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodySession = call.receive<StudentNote.New>()
                    val student = studentNoteService.userUpdate(token, bodySession)
                    call.respond(student)
                }
            }
        }
    }
}
