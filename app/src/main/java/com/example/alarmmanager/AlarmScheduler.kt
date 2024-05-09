// AlarmScheduler.kt
package com.example.alarmmanager

interface AlarmScheduler {
    fun scheduler(item: AlarmItem)
    fun cancel(item: AlarmItem)
}