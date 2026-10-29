package com.eaglenews.app.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val dateFormatter: SimpleDateFormat
    get() = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

val timeFormatter: SimpleDateFormat
    get() = SimpleDateFormat("h:mm a", Locale.getDefault())

fun startOfDay(timestamp: Timestamp?): Date {
    val date = timestamp?.toDate() ?: Date()

    return Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

fun isBeforeToday(date: Date): Boolean {
    return date.before(startOfDay(Timestamp.now()))
}