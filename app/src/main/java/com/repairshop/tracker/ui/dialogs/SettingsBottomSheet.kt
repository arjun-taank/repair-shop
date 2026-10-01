package com.repairshop.tracker.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.BottomSheetSettingsBinding

class SettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSettingsBinding? = null
    private val binding get() = _binding!!

    var onSettingsSavedListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = RepairShopApp.prefManager

        // Populate with dynamic values stored in SharedPreferences
        binding.etSettingsUserName.setText(pref.userName)
        binding.etSettingsShopName.setText(pref.shopName)
        binding.etSettingsPhone.setText(pref.phone)
        binding.etSettingsCurrency.setText(pref.currencySymbol)
        binding.switchOfflineMode.isChecked = pref.isOfflineMode

        binding.btnCloseSettings.setOnClickListener {
            dismiss()
        }

        binding.btnSaveSettings.setOnClickListener {
            val userName = binding.etSettingsUserName.text?.toString()?.trim()
            val shopName = binding.etSettingsShopName.text?.toString()?.trim()
            val phone = binding.etSettingsPhone.text?.toString()?.trim() ?: ""
            val currency = binding.etSettingsCurrency.text?.toString()?.trim() ?: "₹"
            val isOffline = binding.switchOfflineMode.isChecked

            if (userName.isNullOrEmpty()) {
                binding.tilSettingsUserName.error = "Name is required"
                return@setOnClickListener
            }
            binding.tilSettingsUserName.error = null

            if (shopName.isNullOrEmpty()) {
                binding.tilSettingsShopName.error = "Shop name is required"
                return@setOnClickListener
            }
            binding.tilSettingsShopName.error = null

            // Save dynamically into SharedPreferences
            pref.updateProfile(
                userName = userName,
                shopName = shopName,
                phone = phone,
                currency = if (currency.isNotEmpty()) currency else "₹",
                isOffline = isOffline
            )

            Toast.makeText(requireContext(), "Settings saved successfully!", Toast.LENGTH_SHORT).show()
            onSettingsSavedListener?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SettingsBottomSheet"
        fun newInstance(onSaved: (() -> Unit)? = null): SettingsBottomSheet {
            return SettingsBottomSheet().apply {
                onSettingsSavedListener = onSaved
            }
        }
    }
}
