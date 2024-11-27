package com.denizcan.taskmanager.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.broadcast.TaskReminderReceiver
import com.denizcan.taskmanager.service.TaskReminderService
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar'ı ActionBar olarak ayarla
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // NavController ile bağla
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        // Bildirim izni kontrolü (Android 13 ve üzeri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            }
        }

        // Alarmı kurma
        setTaskReminderAlarm()
        startTaskReminderService()
        scheduleTaskReminderJob()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Kullanıcıya bildirim izni istemek için metod
    private fun requestNotificationPermission() {
        // Kullanıcıya açıklama gösterebiliriz
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, "We need notification permission to remind you about tasks.", Toast.LENGTH_SHORT).show()
        }
        // İzin istemek
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // İzin isteme işlemi için ActivityResultLauncher
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    // Alarm kurma (TaskReminderReceiver'ı tetiklemek için)
    private fun setTaskReminderAlarm() {
        // Check if the app can schedule exact alarms
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(this, TaskReminderReceiver::class.java)

                // FLAG_IMMUTABLE ekleyelim (Değiştirilemez PendingIntent)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE // ya da FLAG_MUTABLE, ihtiyaca göre
                )

                // Alarmın ne zaman tetikleneceğine dair bir zaman belirleyelim (örneğin, 1 dakikalık bir süre sonrası)
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    add(Calendar.MINUTE, 1)  // 1 dakika sonrası
                }

                // Alarmı kurma
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                Toast.makeText(this, "This app cannot schedule exact alarms. Please enable the necessary permissions.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // For older versions, you can schedule the alarm without needing the exact alarm permission
            val intent = Intent(this, TaskReminderReceiver::class.java)

            // FLAG_IMMUTABLE ekleyelim (Değiştirilemez PendingIntent)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // ya da FLAG_MUTABLE, ihtiyaca göre
            )

            // Alarmın ne zaman tetikleneceğine dair bir zaman belirleyelim (örneğin, 1 dakikalık bir süre sonrası)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.MINUTE, 1)  // 1 dakika sonrası
            }

            // Alarmı kurma
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun startTaskReminderService() {
        val serviceIntent = Intent(this, TaskReminderService::class.java)
        startService(serviceIntent)
    }

    private fun scheduleTaskReminderJob() {
        val componentName = ComponentName(this, TaskReminderService::class.java)

        val jobInfo = JobInfo.Builder(123, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setPeriodic(15 * 60 * 1000) // 15 dakikada bir çalışsın
            .build()

        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

}
