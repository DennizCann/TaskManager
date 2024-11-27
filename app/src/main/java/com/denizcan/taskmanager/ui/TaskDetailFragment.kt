package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentTaskDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        // Database helper'ı başlatıyoruz
        databaseHelper = TaskDatabaseHelper(requireContext())

        // Görev detaylarını alıyoruz
        task = arguments?.getSerializable("task") as? Task

        // Eğer görev varsa, detayları gösteriyoruz
        task?.let {
            binding.txtTaskName.text = it.name
            binding.txtTaskDescription.text = it.description
            // Due Date kontrolü yapıyoruz ve null kontrolü ekliyoruz
            binding.txtTaskDueDate.text = if (it.dueDate != null) {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it.dueDate!!))
            } else {
                "Due Date: Not Set"  // Eğer dueDate null ise "Not Set" yazdırıyoruz
            }
            binding.txtTaskCategory.text = "Category: ${it.category ?: "Not Set"}"  // "Not Set" yerine gerçek kategori gösterilir
        }


        // Silme butonuna tıklanıldığında TaskDeleteDialogFragment açılacak
        binding.btnDeleteTask.setOnClickListener {
            task?.let {
                // DialogFragment'ı oluştur
                val deleteDialog = TaskDeleteDialogFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("task", it)  // Görevi DialogFragment'a gönder
                    }
                    // Silme onaylandığında yapılacak işlemi tanımlıyoruz
                    setOnDeleteConfirmedListener {
                        // Görev silme işlemi
                        databaseHelper.deleteTask(it.id)
                        findNavController().navigateUp() // Listeye geri dön
                        Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                // DialogFragment'ı göster
                deleteDialog.show(parentFragmentManager, "TaskDeleteDialogFragment")
            }
        }

        // Düzenleme butonuna tıklanıldığında görev düzenleme ekranına geçiş yapılır
        binding.btnEditTask.setOnClickListener {
            task?.let {
                val bundle = Bundle().apply {
                    putSerializable("task", it) // Mevcut görevi düzenleme için gönderiyoruz
                }
                findNavController().navigate(R.id.action_taskDetailFragment_to_addTaskFragment, bundle)
            }
        }

        // Kategori düzenleme butonuna tıklanıldığında BottomSheet'i başlatıyoruz
        binding.btnEditCategory.setOnClickListener {
            task?.let {
                // BottomSheetDialogFragment'ı başlatıyoruz
                val bottomSheetFragment = CategoryBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("task", it)  // Görevi BottomSheet'e gönderiyoruz
                    }
                }
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
