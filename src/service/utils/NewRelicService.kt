package com.progressp.service.utils

import com.newrelic.api.agent.NewRelic

object MetricNames {
    const val USERS_REGISTERED = "Custom/BusinessData/UsersRegistered"
    const val USERS_DELETED = "Custom/BusinessData/UsersDeleted"
    const val USERS_LOGGED = "Custom/BusinessData/UsersLogged"
}

interface INewRelicService {
    suspend fun incrementMetric(metricName: String)
    suspend fun recordMetric(metricName: String, metricValue: Float)
}

class NewRelicService : INewRelicService {
    override suspend fun incrementMetric(metricName: String) {
        NewRelic.incrementCounter(metricName)
    }

    override suspend fun recordMetric(metricName: String, metricValue: Float) {
        NewRelic.recordMetric(metricName, metricValue)
    }
}
