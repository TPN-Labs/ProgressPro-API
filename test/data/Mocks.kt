package com.progressp.data

import com.progressp.data.MockUUIDs.sessionList
import com.progressp.data.MockUUIDs.studentList
import com.progressp.data.MockUUIDs.userList
import com.progressp.models.student.Student
import com.progressp.models.student.StudentSession
import com.progressp.models.user.User
import java.util.*

object MockUUIDs {
    val userList = listOf(
        "21611e7c-3966-4720-946b-4351d4d9da32",
        "9931f4f6-ce12-465b-8a10-ad35e7fbbbae",
        "08743801-250d-4a80-9578-630afd094689"
    )

    val studentList = listOf(
        "e06c6e21-30ec-4453-80b8-ab70a489228f",
        "641fa276-741d-4d2a-9e53-d475afc54d9c"
    )

    val sessionList = listOf(
        "11a69f18-e158-494d-8357-e8af1da2e120",
        "9c0ca8ee-bc32-11ed-afa1-0242ac120002"
    )
}

object MockData {
    val newUser: User.Register = User.Register(
        userList[0], "mock@email.com", "mock-username", "mock-pass"
    )
    val newStudent: Student.New = Student.New(
        studentList[0], "Mock Name", 1, 1, 1.55, "2022-01-01"
    )
    val newSession: StudentSession.New = StudentSession.New(
        sessionList[0], studentList[0], 1, 200, 12
    )
}