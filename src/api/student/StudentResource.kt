package com.progressp.api.student

import com.progressp.database.IDatabaseFactory
import com.progressp.models.student.Student
import com.progressp.service.student.IStudentService
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
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.studentsApi(studentService: IStudentService, databaseFactory: IDatabaseFactory) {

    userValidated(databaseFactory) {
        route("/students") {
            newRelicTrace(
                NewRelicTracing("Student", "All")
            ) {
                get("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val preferences = studentService.userAll(token)
                    call.respond(preferences)
                }
            }

            newRelicTrace(
                NewRelicTracing("Student", "Create")
            ) {
                post("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodyStudent = call.receive<Student.New>()
                    val student = studentService.userCreate(token, bodyStudent)
                    call.respond(student)
                }
            }

            newRelicTrace(
                NewRelicTracing("Student", "Update")
            ) {
                put("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodyStudent = call.receive<Student.New>()
                    val student = studentService.userUpdate(token, bodyStudent)
                    call.respond(student)
                }
            }

            newRelicTrace(
                NewRelicTracing("Student", "Delete")
            ) {
                delete("/my") {
                    val token = call.request.authorization()?.removePrefix("Bearer ")!!
                    val bodyStudent = call.receive<Student.Delete>()
                    val student = studentService.userDelete(token, bodyStudent)
                    call.respond(student)
                }
            }

        }
    }
}
