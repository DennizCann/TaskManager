package com.denizcan.taskmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.databinding.ItemTaskBinding

class TaskAdapter(
    private var taskList: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskCompletionChange: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.txtTaskName.text = task.name
            binding.txtTaskDescription.text = task.description

            // Checkbox durumunu güncelle
            binding.checkboxTaskCompleted.setOnCheckedChangeListener(null) // Önceki dinleyiciyi temizle
            binding.checkboxTaskCompleted.isChecked = task.isCompleted
            binding.checkboxTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                onTaskCompletionChange(task, isChecked)
            }

            // Tıklama ile detaylara git
            binding.root.setOnClickListener {
                onTaskClick(task)
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

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }
}
