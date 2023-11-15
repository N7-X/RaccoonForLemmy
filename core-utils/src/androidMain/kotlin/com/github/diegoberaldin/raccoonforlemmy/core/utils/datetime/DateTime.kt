package com.github.diegoberaldin.raccoonforlemmy.core.utils.datetime

import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.GregorianCalendar

actual object DateTime {
    actual fun epochMillis(): Long = System.currentTimeMillis()

    actual fun getFormattedDate(
        iso8601Timestamp: String,
        format: String,
    ): String {
        val date = getDateFromIso8601Timestamp(iso8601Timestamp)
        val formatter = DateTimeFormatter.ofPattern(format)
        return date.format(formatter)
    }

    actual fun getPrettyDate(
        iso8601Timestamp: String,
        yearLabel: String,
        monthLabel: String,
        dayLabel: String,
        hourLabel: String,
        minuteLabel: String,
        secondLabel: String,
    ): String {
        val now = GregorianCalendar().toZonedDateTime()
        val date = getDateFromIso8601Timestamp(iso8601Timestamp)
        val delta = Period.between(date.toLocalDate(), now.toLocalDate())
        val years = delta.years
        val months = delta.months
        val days = delta.days
        val nowSeconds = now.toEpochSecond()
        val dateSeconds = date.toEpochSecond()
        val diffSeconds = (nowSeconds - dateSeconds)
        val hours = (diffSeconds % 86400) / 3600
        val minutes = (diffSeconds % 3600) / 60
        val seconds = diffSeconds % 60
        return when {
            years >= 1 -> buildString {
                append("${years}$yearLabel")
                if (months >= 1) {
                    append(" ${months}$monthLabel")
                }
                if (days >= 1) {
                    append(" ${days}$dayLabel")
                }
            }

            months >= 1 -> buildString {
                append("${months}$monthLabel")
                if (days >= 1) {
                    append(" ${days}$dayLabel")
                }
            }

            days >= 1 -> buildString {
                append("${days}$dayLabel")
            }

            hours >= 1 -> buildString {
                append(" ${hours}$hourLabel")
            }

            minutes >= 1 -> buildString {
                append(" ${minutes}$minuteLabel")
            }

            else -> buildString {
                append(" ${seconds}$secondLabel")
            }
        }
    }

    private fun getDateFromIso8601Timestamp(string: String): ZonedDateTime {
        return ZonedDateTime.parse(string)
    }
}