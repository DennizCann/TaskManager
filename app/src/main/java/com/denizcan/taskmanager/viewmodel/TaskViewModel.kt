package com.denizcan.taskmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denizcan.taskmanager.data.Task

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>?>()
    val tasks: MutableLiveData<List<Task>?> get() = _tasks

    private val _filteredTasks = MutableLiveData<List<Task>>()
    val filteredTasks: LiveData<List<Task>> get() = _filteredTasks

    fun setTasks(taskList: List<Task>) {
        _tasks.value = taskList
        _filteredTasks.value = taskList // Varsayılan olarak tüm görevler
    }

    fun filterTasks(showCompleted: Boolean?) {
        _filteredTasks.value = _tasks.value?.filter { task ->
            when (showCompleted) {
                true -> task.isCompleted
                false -> !task.isCompleted
                else -> true
            }
        }
    }

    fun updateTask(updatedTask: Task) {
        val currentTasks = _tasks.value?.map { task ->
            if (task.id == updatedTask.id) updatedTask else task
        }
        _tasks.value = currentTasks
        filterTasks(null) // Filtreyi yeniden uygula
    }
}
