package com.denizcan.taskmanager.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_DUE_DATE INTEGER
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COLUMN_DUE_DATE INTEGER")
        }
    }


    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COLUMN_DUE_DATE, task.dueDate)
        }
        val result = db.insert(TABLE_TASKS, null, values)
        db.close()
        return result
    }
    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID=?", arrayOf(taskId.toString()))
        db.close()
    }

    fun updateTask(task: Task) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COLUMN_DUE_DATE, task.dueDate)
        }
        db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
        db.close()
    }
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(TABLE_TASKS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                    dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasks
    }

    companion object {
        private const val DATABASE_NAME = "task_manager.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_DUE_DATE = "due_date"
    }
}
