package com.repairshop.tracker.ui.customers

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.ActivityAddCustomerBinding
import com.repairshop.tracker.model.Customer

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSaveButton()
    }

    private fun setupToolbar() {
        binding.toolbarAddCustomer.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveCustomer.setOnClickListener {
            val name = binding.etCustomerName.text?.toString()?.trim()
            val phone = binding.etCustomerPhone.text?.toString()?.trim()
            val address = binding.etCustomerAddress.text?.toString()?.trim() ?: ""

            if (name.isNullOrEmpty()) {
                binding.tilCustomerName.error = "Please enter customer name"
                return@setOnClickListener
            }
            binding.tilCustomerName.error = null

            if (phone.isNullOrEmpty()) {
                binding.tilCustomerPhone.error = "Please enter phone number"
                return@setOnClickListener
            }
            binding.tilCustomerPhone.error = null

            val customer = Customer(
                name = name,
                phone = phone,
                address = address,
                totalCharge = 0.0,
                paidAdvance = 0.0
            )

            RepairShopApp.database.insertCustomer(customer)
            Toast.makeText(this, "Customer saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
