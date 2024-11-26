package com.denizcan.taskmanager.broadcast

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.denizcan.taskmanager.R

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Bildirim izinlerini kontrol et (Android 13 ve üzeri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // İzin verilmemiş, bildirim gönderilemez
                Toast.makeText(context, "Permission to post notifications is not granted", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Bildirim kanalı oluşturulması gerekiyor (Android 8.0 ve üzeri için)
        createNotificationChannel(context)

        // Bildirim oluştur
        val notification: Notification = NotificationCompat.Builder(context, "taskReminderChannel")
            .setSmallIcon(R.drawable.ic_notification)  // Kendi simgenizi buraya koyun
            .setContentTitle("Task Reminder")
            .setContentText("You have a task coming up soon!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Notification Manager ile bildirimi göster
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }

    // Bildirim kanalını oluşturma (Android 8.0 ve üzeri için)
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminder Channel"
            val descriptionText = "Channel for task reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("taskReminderChannel", name, importance).apply {
                description = descriptionText
            }

            // Kanalı sistemde kaydet
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
