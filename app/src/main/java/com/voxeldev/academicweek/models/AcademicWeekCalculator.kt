package com.voxeldev.academicweek.models

import android.content.Context
import com.voxeldev.academicweek.R
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class AcademicWeekCalculator(private val context: Context) {

    fun calculateWeek(countStart : Long) : AcademicWeek {
        if (countStart == -1L)
            throw Exception(context.getString(R.string.count_date_not_specified))

        val countStartDate = getMonday(LocalDateTime.ofEpochSecond(
            countStart, 0, ZoneOffset.UTC))
        val nowDate = getMonday(LocalDateTime.now())

        if (ChronoUnit.DAYS.between(countStartDate, nowDate) < 0)
            throw Exception(context.getString(R.string.week_not_started))

        return AcademicWeek(ChronoUnit.WEEKS.between(countStartDate, nowDate) + 1,
        String.format(
            context.getString(R.string.counting_from),
            countStartDate.dayOfMonth,
            countStartDate.monthValue))
    }

    private fun getMonday(localDateTime: LocalDateTime) : LocalDateTime {
        var tempLocalDateTime = localDateTime

        while (tempLocalDateTime.dayOfWeek != DayOfWeek.MONDAY) {
            tempLocalDateTime = tempLocalDateTime.minusDays(1)
        }

        return tempLocalDateTime
    }
}