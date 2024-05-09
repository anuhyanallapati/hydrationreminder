// AlarmReceiver.kt
package com.example.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.provider.Settings

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        showNotification(context, message)
        println("Alarm triggered: $message")  // Debug statement
    }

    private fun showNotification(context: Context?, message: String) {
        println("In showNotification alarm triggered: $message")  // Debug statement
        if (NotificationManagerCompat.from(context!!).areNotificationsEnabled()) {
            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.water_bottle)
                .setContentTitle("Hydration Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                println("show notification, alarm triggered?")  // Debug statement
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            println("popup, alarm triggered?")  // Debug statement
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        }
    }


    private fun createNotificationChannel(context: Context?) {
        println("createNotificationChannel alarm triggered")  // Debug statement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Alarm Notifications"
            }
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 123
    }

}
