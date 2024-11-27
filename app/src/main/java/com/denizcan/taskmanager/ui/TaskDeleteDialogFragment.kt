package com.denizcan.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.denizcan.taskmanager.data.Task
import com.denizcan.taskmanager.databinding.FragmentTaskDeleteDialogBinding

class TaskDeleteDialogFragment : DialogFragment() {

    private var _binding: FragmentTaskDeleteDialogBinding? = null
    private val binding get() = _binding!!

    private var onDeleteConfirmed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Görev bilgisi al
        val task = arguments?.getSerializable("task") as? Task

        // Eğer task varsa, uygun mesajı göster
        task?.let {
            binding.tvDeleteTaskMessage.text = "Are you sure you want to delete task: ${it.name}?"
        }

        // Onay butonuna tıklanırsa
        binding.btnConfirmDelete.setOnClickListener {
            onDeleteConfirmed?.invoke()  // Silme işlemi onaylandı
            dismiss()  // Dialog'u kapat
        }

        // İptal butonuna tıklanırsa
        binding.btnCancelDelete.setOnClickListener {
            dismiss()  // Dialog'u kapat
        }
    }

    fun setOnDeleteConfirmedListener(listener: () -> Unit) {
        onDeleteConfirmed = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
