// AndroidAlarmScheduler.kt
package com.example.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduler(item: AlarmItem) {
        var nextAlarmTime = item.startTime

        while (nextAlarmTime <= item.endTime) {
            val epochTime = nextAlarmTime.atDate(LocalDateTime.now().toLocalDate())
                .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("EXTRA_MESSAGE", item.message)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                nextAlarmTime.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                epochTime,
                pendingIntent
            )

            nextAlarmTime = nextAlarmTime.plusMinutes(item.intervalMinutes.toLong())
        }

        println("Start hydration reminders called, alarms yet to be triggered") // Debug statement
    }

    override fun cancel(item: AlarmItem) {
        var currentAlarmTime = item.startTime
        while (currentAlarmTime <= item.endTime) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                currentAlarmTime.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            currentAlarmTime = currentAlarmTime.plusMinutes(item.intervalMinutes.toLong())
        }

        println("Cancel hydration reminders called, alarms triggered?") // Debug statement
    }
}