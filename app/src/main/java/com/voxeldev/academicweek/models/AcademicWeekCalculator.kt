package com.voxeldev.academicweek.models

import android.content.Context
import com.voxeldev.academicweek.R
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class AcademicWeekCalculator(private val context: Context) {

    fun calculateWeek(countStart: Long, countEnd: Long): AcademicWeek {
        if (countStart == -1L)
            throw Exception(context.getString(R.string.count_date_not_specified))

        val countStartDate = getMonday(
            LocalDateTime.ofEpochSecond(
                countStart, 0, ZoneOffset.UTC
            )
        )
        val countEndDate = getMonday(
            LocalDateTime.ofEpochSecond(
                countEnd, 0, ZoneOffset.UTC
            ), false
        )
        val nowDate = getMonday(LocalDateTime.now())

        if (ChronoUnit.DAYS.between(countStartDate, nowDate) < 0)
            throw Exception(context.getString(R.string.week_not_started))

        val currentWeek = ChronoUnit.WEEKS.between(countStartDate, nowDate) + 1
        val totalWeeks = ChronoUnit.WEEKS.between(countStartDate, countEndDate) + 1

        if (currentWeek > totalWeeks)
            throw Exception(context.getString(R.string.study_is_over))

        return AcademicWeek(
            currentWeek,
            totalWeeks,
            context.getString(
                R.string.counting_from,
                countStartDate.dayOfMonth,
                countStartDate.monthValue
            ),
            context.getString(
                R.string.counting_until,
                countEndDate.dayOfMonth,
                countEndDate.monthValue
            ),
        )
    }

    private fun getMonday(localDateTime: LocalDateTime, isNegativeDirection: Boolean = true)
            : LocalDateTime {
        var tempLocalDateTime = localDateTime

        while (tempLocalDateTime.dayOfWeek != DayOfWeek.MONDAY) {
            tempLocalDateTime =
                if (isNegativeDirection) tempLocalDateTime.minusDays(1)
                else tempLocalDateTime.plusDays(1)
        }

        return tempLocalDateTime
    }
}