package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: TaskDatabaseHelper
    private var task: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = TaskDatabaseHelper(requireContext())
        task = arguments?.getSerializable("task") as? Task

        task?.let {
            binding.txtTaskName.text = it.name
            binding.txtTaskDescription.text = it.description
        }

        binding.btnDeleteTask.setOnClickListener {
            task?.let {
                databaseHelper.deleteTask(it.id)
                findNavController().navigateUp() // Listeye geri dön
            }
        }

        binding.btnEditTask.setOnClickListener {
            task?.let {
                val bundle = Bundle().apply {
                    putSerializable("task", it)
                }
                findNavController().navigate(R.id.action_taskDetailFragment_to_addTaskFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
