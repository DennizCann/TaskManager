package com.denizcan.taskmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denizcan.taskmanager.data.Task

class TaskViewModel : ViewModel() {
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    fun setTasks(tasks: List<Task>) {
        _taskList.value = tasks
    }

    fun getTasks(): List<Task> {
        return _taskList.value ?: emptyList()
    }
}
