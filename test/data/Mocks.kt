package com.progressp.data

import com.progressp.data.MockUUIDs.userList
import com.progressp.models.user.User

object MockUUIDs {
    val userList = listOf(
        "21611e7c-3966-4720-946b-4351d4d9da32",
        "9931f4f6-ce12-465b-8a10-ad35e7fbbbae",
        "08743801-250d-4a80-9578-630afd094689",
        "e06c6e21-30ec-4453-80b8-ab70a489228f",
        "641fa276-741d-4d2a-9e53-d475afc54d9c"
    )
}

object MockData {
    val newUser: User.Register = User.Register(userList[0], "mock@email.com", "mock-username", "mock-pass")
}