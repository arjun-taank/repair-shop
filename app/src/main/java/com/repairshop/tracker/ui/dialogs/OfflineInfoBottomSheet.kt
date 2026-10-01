package com.repairshop.tracker.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.repairshop.tracker.databinding.BottomSheetOfflineInfoBinding

class OfflineInfoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOfflineInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOfflineInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = com.repairshop.tracker.RepairShopApp.prefManager
        if (pref.isOfflineMode) {
            binding.btnSyncStatus.text = "Switch to Online Mode"
        } else {
            binding.btnSyncStatus.text = "Switch to Offline Mode"
        }

        binding.btnSyncStatus.setOnClickListener {
            pref.isOfflineMode = !pref.isOfflineMode
            val newMode = if (pref.isOfflineMode) "Offline Mode" else "Online Mode"
            Toast.makeText(requireContext(), "$newMode activated!", Toast.LENGTH_SHORT).show()
            (activity as? com.repairshop.tracker.ui.main.MainActivity)?.refreshDynamicUI()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "OfflineInfoBottomSheet"
        fun newInstance() = OfflineInfoBottomSheet()
    }
}
