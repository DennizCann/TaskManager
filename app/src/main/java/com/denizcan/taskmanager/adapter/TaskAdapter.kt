package com.denizcan.taskmanager.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.databinding.ItemTaskBinding


class TaskAdapter(
    private var taskList: MutableList<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskCompletionChange: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.txtTaskName.text = task.name
            binding.checkboxTaskCompleted.isChecked = task.isCompleted

            // Göreve tıklama
            binding.root.setOnClickListener {
                onTaskClick(task)
            }

            // Checkbox değişimi
            binding.checkboxTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                // Direkt adapter güncellemesi yerine bu durumu üst sınıfa bildir
                Handler(Looper.getMainLooper()).post {
                    onTaskCompletionChange(task, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(tasks: List<Task>) {
        taskList = tasks.toMutableList()
        notifyDataSetChanged() // Tüm listeyi güncellemek gerekirse
    }

    fun updateTask(task: Task) {
        val index = taskList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            taskList[index] = task
            notifyItemChanged(index) // Sadece ilgili öğeyi güncelle
        }
    }
}

