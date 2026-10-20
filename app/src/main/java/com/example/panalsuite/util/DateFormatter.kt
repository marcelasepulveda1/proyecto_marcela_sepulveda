package com.example.panalsuite.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Date helpers for the ticket screens (Chilean Spanish format). */
object DateFormatter {

    private val locale: Locale = Locale.forLanguageTag("es-CL")

    fun full(millis: Long): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", locale).format(Date(millis))

    /** E.g. "hace 2 horas", "ayer". */
    fun relative(millis: Long, now: Long = System.currentTimeMillis()): String =
        DateUtils.getRelativeTimeSpanString(millis, now, DateUtils.MINUTE_IN_MILLIS).toString()
}
