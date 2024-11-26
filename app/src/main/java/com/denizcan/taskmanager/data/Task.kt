package com.denizcan.taskmanager.data

import java.io.Serializable

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    var isCompleted: Boolean // var olarak değiştirildi
):Serializable
