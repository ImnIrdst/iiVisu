package com.imn.ivisusample.utils

import java.util.concurrent.TimeUnit

fun Long.formatAsTime(): String {
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(this) % 60).toInt()
    val minutes = (TimeUnit.MILLISECONDS.toMinutes(this) % 60).toInt()

    return when (val hours = (TimeUnit.MILLISECONDS.toHours(this)).toInt()) {
        0 -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}