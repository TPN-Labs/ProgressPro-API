package com.progressp

import com.progressp.database.DatabaseFactory
import com.progressp.database.IDatabaseFactory
import com.progressp.service.student.IStudentMeetingService
import com.progressp.service.student.IStudentService
import com.progressp.service.student.IStudentSessionService
import com.progressp.service.student.StudentMeetingService
import com.progressp.service.student.StudentService
import com.progressp.service.student.StudentSessionService
import com.progressp.service.user.IPreferenceService
import com.progressp.service.user.IUserService
import com.progressp.service.user.PreferenceService
import com.progressp.service.user.UserService
import org.koin.dsl.module

val serviceKoinModule = module {
    single<IUserService> { UserService(get()) }
    single<IPreferenceService> { PreferenceService(get()) }
    single<IStudentService> { StudentService(get()) }
    single<IStudentSessionService> { StudentSessionService(get()) }
    single<IStudentMeetingService> { StudentMeetingService(get()) }
}

val databaseKoinModule = module {
    single<IDatabaseFactory> { DatabaseFactory() }
}
