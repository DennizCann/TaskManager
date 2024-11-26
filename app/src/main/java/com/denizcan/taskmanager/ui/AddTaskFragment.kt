package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: TaskDatabaseHelper
    private var task: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = TaskDatabaseHelper(requireContext())
        task = arguments?.getSerializable("task") as? Task

        task?.let {
            binding.edtTaskName.setText(it.name)
            binding.edtTaskDescription.setText(it.description)
        }

        binding.btnSaveTask.setOnClickListener {
            val taskName = binding.edtTaskName.text.toString()
            val taskDescription = binding.edtTaskDescription.text.toString()

            if (task != null) {
                // Düzenleme modu
                val updatedTask = task!!.copy(
                    name = taskName,
                    description = taskDescription
                )
                databaseHelper.updateTask(updatedTask)
            } else {
                // Yeni görev ekleme
                val newTask = Task(
                    id = 0, // AutoIncrement için 0
                    name = taskName,
                    description = taskDescription,
                    isCompleted = false
                )
                databaseHelper.insertTask(newTask)
            }

            findNavController().navigateUp() // Listeye geri dön
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
