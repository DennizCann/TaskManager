package com.denizcan.taskmanager.data

import java.io.Serializable

data class Task(
    val id: Int = 0,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null // Tarih ve saat bilgisi (Unix Timestamp)
):Serializable