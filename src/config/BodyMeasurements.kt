package com.progressp.config

enum class MeasurementCode(val code: String) {
    WEIGHT("WEIGHT"),
    WAIST("WAIST"),
    HIP("HIP"),
    FEMUR("FEMUR"),
    ARM("ARM")
}

object Measurements {
    private val allMeasurements: List<MeasurementCode> = MeasurementCode.values().toList()

    fun getAll(): List<MeasurementCode> {
        return allMeasurements
    }
}
