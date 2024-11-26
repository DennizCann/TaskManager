package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.adapter.TaskAdapter
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentTaskListBinding
import com.denizcan.taskmanager.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseHelper = TaskDatabaseHelper(requireContext())

        setupRecyclerView(databaseHelper)
        setupRadioButtons()
        setupFab()

        // Veritabanından görevleri al ve ViewModel'e gönder
        viewModel.setTasks(databaseHelper.getAllTasks())

        // Görev listesi güncellemelerini gözlemle
        viewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

    private fun setupRecyclerView(databaseHelper: TaskDatabaseHelper) {
        adapter = TaskAdapter(
            taskList = mutableListOf(),
            onTaskClick = { task -> navigateToDetail(task) },
            onTaskCompletionChange = { task, isCompleted ->
                val updatedTask = task.copy(isCompleted = isCompleted)
                databaseHelper.updateTask(updatedTask)
                viewModel.updateTask(updatedTask)
                Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter
    }

    private fun setupRadioButtons() {
        binding.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAllTasks -> viewModel.filterTasks(null)
                R.id.radioCompletedTasks -> viewModel.filterTasks(true)
                R.id.radioIncompleteTasks -> viewModel.filterTasks(false)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment)
        }
    }

    private fun navigateToDetail(task: Task) {
        val bundle = Bundle().apply {
            putSerializable("task", task)
        }
        findNavController().navigate(R.id.action_taskListFragment_to_taskDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}