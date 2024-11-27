package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.denizcan.taskmanager.R
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.data.TaskDatabaseHelper
import com.denizcan.taskmanager.databinding.FragmentCategoryBottomSheetBinding

class CategoryBottomSheetFragment : DialogFragment() {

    private var _binding: FragmentCategoryBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var task: Task
    private lateinit var databaseHelper: TaskDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = TaskDatabaseHelper(requireContext())

        // Görev verilerini alıyoruz
        task = arguments?.getSerializable("task") as Task

        // Kategori seçimleri için seçenekler
        val categories = listOf("Work", "Personal", "Shopping", "Others")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_category, categories)  // Kendi layout'umuzu kullanıyoruz
        binding.listViewCategory.adapter = adapter

        binding.listViewCategory.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            task.category = selectedCategory  // Kategoriyi görevde güncelliyoruz

            // Veritabanında kategoriyi güncelliyoruz
            databaseHelper.updateTask(task)

            // Kategori güncellemesi başarılı mesajı
            Toast.makeText(requireContext(), "Category updated to $selectedCategory", Toast.LENGTH_SHORT).show()

            // BottomSheet'i kapatıyoruz
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
