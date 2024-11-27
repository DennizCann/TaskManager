package com.denizcan.taskmanager.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.denizcan.taskmanager.R

class TaskReminderService : IntentService("TaskReminderService") {

    override fun onHandleIntent(intent: Intent?) {
        // Görevlerin durumunu kontrol et ve hatırlatıcı bildirim gönder
        sendTaskReminderNotification()
    }

    private fun sendTaskReminderNotification() {
        // Bildirim kanalı oluşturulması gerekiyor (Android 8.0 ve üzeri için)
        createNotificationChannel()

        // Bildirim oluştur
        val notification: Notification = NotificationCompat.Builder(this, "taskReminderChannel")
            .setSmallIcon(R.drawable.ic_notification) // Kendi simgenizi kullanın
            .setContentTitle("Task Reminder")
            .setContentText("You have pending tasks!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Notification Manager ile bildirimi göster
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminder Channel"
            val descriptionText = "Channel for task reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("taskReminderChannel", name, importance).apply {
                description = descriptionText
            }

            // Kanalı kaydet
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
