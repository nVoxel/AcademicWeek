package com.voxeldev.academicweek.models

data class AcademicWeek(
    val week: Long,
    val totalWeeks: Long,
    val countingFrom: String,
    val countingUntil: String
)