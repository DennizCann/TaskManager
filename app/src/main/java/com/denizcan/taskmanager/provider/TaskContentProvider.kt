package com.denizcan.taskmanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import java.util.*

class TaskContentProvider : ContentProvider() {

    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = TaskDatabaseHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        // Veritabanından görevleri sorgula
        return dbHelper.readableDatabase.query("tasks", projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Veritabanına yeni görev ekle
        val id = dbHelper.writableDatabase.insert("tasks", null, values)

        // Görevi takvime ekle
        values?.let { addTaskToCalendar(context, it) }

        return Uri.parse("content://com.denizcan.taskmanager.provider/tasks/$id")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        // Veritabanında görev güncelle
        return dbHelper.writableDatabase.update("tasks", values, selection, selectionArgs)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Veritabanında görev sil
        return dbHelper.writableDatabase.delete("tasks", selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/vnd.com.denizcan.taskmanager.tasks"
    }

    // Takvime görev ekleme fonksiyonu
    private fun addTaskToCalendar(context: Context?, taskValues: ContentValues) {
        if (context == null) return

        val calendar = Calendar.getInstance()
        val startMillis: Long = calendar.timeInMillis
        val endMillis: Long = startMillis + 60 * 60 * 1000  // 1 saatlik etkinlik

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, taskValues.getAsString("name"))
            put(CalendarContract.Events.DESCRIPTION, taskValues.getAsString("description"))
            put(CalendarContract.Events.CALENDAR_ID, 1)  // Varsayılan takvim
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.let {
                // Veritabanına başarıyla ekleme işlemi
                Toast.makeText(context, "Task added to calendar", Toast.LENGTH_SHORT).show()

                // Takvimi hemen güncellemek için (Opsiyonel)
                val contentUri = CalendarContract.Events.CONTENT_URI
                context.contentResolver.notifyChange(contentUri, null)  // Takvimi güncelle
            }
        } catch (e: Exception) {
            Log.e("AddTaskFragment", "Error adding task to calendar", e)
            Toast.makeText(context, "Failed to add task to calendar", Toast.LENGTH_SHORT).show()
        }
    }

}
