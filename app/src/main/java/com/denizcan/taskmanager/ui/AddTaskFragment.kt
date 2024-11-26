package com.denizcan.taskmanager.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private var dueDate: Long? = null

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

        binding.btnSetDueDate.setOnClickListener { showDateTimePicker() }


        databaseHelper = TaskDatabaseHelper(requireContext())
        task = arguments?.getSerializable("task") as? Task

        task?.let {
            binding.edtTaskName.setText(it.name)
            binding.edtTaskDescription.setText(it.description)
        }

        binding.btnSaveTask.setOnClickListener {
            val taskName = binding.edtTaskName.text.toString()
            val taskDescription = binding.edtTaskDescription.text.toString()

            if (taskName.isEmpty() || taskDescription.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                    isCompleted = false,
                )
                databaseHelper.insertTask(newTask)
            }

            findNavController().navigateUp() // Listeye geri dön
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                dueDate = calendar.timeInMillis
                binding.txtDueDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(calendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}