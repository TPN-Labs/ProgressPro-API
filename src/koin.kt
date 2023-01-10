package com.progressp

import com.progressp.database.DatabaseFactory
import com.progressp.database.IDatabaseFactory
import com.progressp.service.user.IPreferenceService
import com.progressp.service.user.IUserService
import com.progressp.service.user.PreferenceService
import com.progressp.service.user.UserService
import org.koin.dsl.module

val serviceKoinModule = module {
    single<IUserService> { UserService(get()) }
    single<IPreferenceService> { PreferenceService(get()) }
}

val databaseKoinModule = module {
    single<IDatabaseFactory> { DatabaseFactory() }
}