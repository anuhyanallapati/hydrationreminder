//AlarmItem.kt
package com.example.alarmmanager

import java.time.LocalTime

data class AlarmItem(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val intervalMinutes: Int,
    val message: String
)