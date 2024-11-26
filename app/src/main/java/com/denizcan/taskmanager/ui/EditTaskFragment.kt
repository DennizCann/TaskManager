package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentEditTaskFragmnetBinding

class EditTaskFragment : Fragment() {

    private var _binding: FragmentEditTaskFragmnetBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: TaskDatabaseHelper
    private var taskId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskFragmnetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = TaskDatabaseHelper(requireContext())

        arguments?.let {
            taskId = it.getInt("taskId", 0)
            val taskName = it.getString("taskName", "")
            val taskDescription = it.getString("taskDescription", "")

            binding.edtTaskName.setText(taskName)
            binding.edtTaskDescription.setText(taskDescription)
        }

        binding.btnSaveTask.setOnClickListener {
            val updatedName = binding.edtTaskName.text.toString()
            val updatedDescription = binding.edtTaskDescription.text.toString()

            if (updatedName.isNotEmpty() && updatedDescription.isNotEmpty()) {
                val updatedTask = Task(taskId, updatedName, updatedDescription, false)
                databaseHelper.updateTask(updatedTask)
                Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}