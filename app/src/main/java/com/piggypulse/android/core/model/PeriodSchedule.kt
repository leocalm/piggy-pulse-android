package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PeriodScheduleResponse(
    val schedule: String, // "manual" or "automatic"
    val startDayOfTheMonth: Int? = null,
    val periodDuration: Int? = null,
    val durationUnit: String? = null,
    val generateAhead: Int? = null,
    val saturdayPolicy: String? = null,
    val sundayPolicy: String? = null,
    val namePattern: String? = null,
    val recurrenceMethod: String? = null,
)

@Serializable
data class CreatePeriodRequest(
    val name: String,
    val startDate: String,
    val periodType: String = "duration",
    val duration: PeriodDuration? = null,
    val manualEndDate: String? = null,
)

@Serializable
data class PeriodDuration(
    val durationUnits: Int,
    val durationUnit: String = "months",
)

@Serializable
data class UpdatePeriodRequest(
    val name: String,
    val startDate: String,
    val periodType: String = "duration",
    val duration: PeriodDuration? = null,
    val manualEndDate: String? = null,
)

@Serializable
data class PeriodResponse(
    val id: String,
    val name: String,
    val startDate: String,
    val status: String,
)
