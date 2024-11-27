package com.denizcan.taskmanager.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
                    description = taskDescription,
                    dueDate = dueDate // Bu kısmı ekledik
                )
                databaseHelper.updateTask(updatedTask)
            } else {
                // Yeni görev ekleme
                val newTask = Task(
                    id = 0, // AutoIncrement için 0
                    name = taskName,
                    description = taskDescription,
                    isCompleted = false,
                    dueDate = dueDate ?: 0L  // Burada dueDate'i null veya 0 (başlangıç değeri) olarak ayarlıyoruz
                )
                databaseHelper.insertTask(newTask)
            }

            findNavController().navigateUp() // Listeye geri dön
        }

    }

    private fun checkAndRequestCalendarPermission(taskName: String, taskDescription: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // İzin verilmemişse kullanıcıdan izin iste
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_CALENDAR),
                REQUEST_CODE_CALENDAR_PERMISSION
            )
        } else {
            // İzin zaten verilmişse, takvime ekleme işlemi yapılabilir
            addTaskToCalendar(taskName, taskDescription)
        }
    }

    // Kullanıcıdan izin alma işlemi tamamlandığında yapılacak işlemi belirleme
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CALENDAR_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, takvime ekleme işlemi yapılabilir
                addTaskToCalendar(task?.name ?: "", task?.description ?: "")
            } else {
                Toast.makeText(context, "Calendar permission is required to add tasks", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTaskToCalendar(taskName: String, taskDescription: String) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, taskName)
            put(CalendarContract.Events.DESCRIPTION, taskDescription)
            put(CalendarContract.Events.DTSTART, System.currentTimeMillis())
            put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 60 * 60 * 1000)  // 1 saat
            put(CalendarContract.Events.CALENDAR_ID, 1)  // Varsayılan takvim
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        try {
            val uri = requireContext().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.let {
                Toast.makeText(context, "Task added to calendar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to add task to calendar", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_CALENDAR_PERMISSION = 101
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
                dueDate = calendar.timeInMillis // Date'i milisaniye cinsinden alıyoruz
                binding.txtDueDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(calendar.time) // Seçilen tarihi uygun formatta gösteriyoruz
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

}
