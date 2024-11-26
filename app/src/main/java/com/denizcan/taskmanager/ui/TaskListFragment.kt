package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.adapter.TaskAdapter
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: TaskDatabaseHelper
    private lateinit var adapter: TaskAdapter
    private var allTasks: List<Task> = emptyList() // Tüm görevler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Veritabanı ve Adapter kurulumu
        databaseHelper = TaskDatabaseHelper(requireContext())
        adapter = TaskAdapter(
            taskList = mutableListOf(),
            onTaskClick = { task -> navigateToDetail(task) },
            onTaskCompletionChange = { task, isCompleted -> updateTaskCompletion(task, isCompleted) }
        )

        // RecyclerView bağlama
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        // Radyo buton dinleyicileri
        binding.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAllTasks -> showTasks(allTasks)
                R.id.radioCompletedTasks -> showTasks(allTasks.filter { it.isCompleted })
                R.id.radioIncompleteTasks -> showTasks(allTasks.filter { !it.isCompleted })
            }
        }

        // FAB tıklama
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment)
        }

        // Görevleri yükle
        loadTaskList()
    }

    private fun loadTaskList() {
        allTasks = databaseHelper.getAllTasks() // Veritabanından tüm görevleri al
        showTasks(allTasks) // Listeyi göster
    }

    private fun showTasks(tasks: List<Task>) {
        adapter.updateTasks(tasks) // Adapter'e yeni görev listesini gönder
    }

    private fun navigateToDetail(task: Task) {
        val bundle = Bundle().apply {
            putSerializable("task", task) // Görevi seri hale getirip detay sayfasına gönder
        }
        findNavController().navigate(R.id.action_taskListFragment_to_taskDetailFragment, bundle)
    }

    private fun updateTaskCompletion(task: Task, isCompleted: Boolean) {
        task.isCompleted = isCompleted
        databaseHelper.updateTask(task)

        // Yalnızca güncellenen görevi adapte edelim
        Handler(Looper.getMainLooper()).post {
            adapter.updateTask(task)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
