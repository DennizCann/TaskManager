package com.denizcan.taskmanager.data

import java.io.Serializable

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val isCompleted: Boolean,
    val dueDate: Long? = null,
    var category: String? = null  // Kategori ekleniyor
):Serializable
