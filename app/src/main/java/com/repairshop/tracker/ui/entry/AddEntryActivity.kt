package com.repairshop.tracker.ui.entry

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.repairshop.tracker.R
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.ActivityAddEntryBinding
import com.repairshop.tracker.model.Customer
import com.repairshop.tracker.model.Item
import com.repairshop.tracker.model.TransactionEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private var isEntryIn: Boolean = true
    private var itemsList: List<Item> = emptyList()
    private var customersList: List<Customer> = emptyList()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupToggles()
        setupDropdowns()
        setupDatePicker()
        setupCurrencyHints()
        setupSaveButton()
    }

    private fun setupCurrencyHints() {
        val curr = RepairShopApp.prefManager.currencySymbol
        binding.tilRepairCharge.hint = "Repair Charge ($curr)"
        binding.tilAdvancePayment.hint = "Advance Paid ($curr)"
    }

    private fun setupToolbar() {
        binding.toolbarAddEntry.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupToggles() {
        binding.btnToggleIn.setOnClickListener {
            if (!isEntryIn) {
                isEntryIn = true
                updateToggleStyles()
            }
        }

        binding.btnToggleOut.setOnClickListener {
            if (isEntryIn) {
                isEntryIn = false
                updateToggleStyles()
            }
        }
    }

    private fun updateToggleStyles() {
        if (isEntryIn) {
            binding.btnToggleIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.stat_green_icon)
            binding.btnToggleIn.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnToggleOut.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.transparent)
            binding.btnToggleOut.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            binding.layoutCharges.visibility = View.VISIBLE
        } else {
            binding.btnToggleOut.backgroundTintList = ContextCompat.getColorStateList(this, R.color.stat_orange_icon)
            binding.btnToggleOut.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnToggleIn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.transparent)
            binding.btnToggleIn.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            binding.layoutCharges.visibility = View.GONE
        }
    }

    private fun setupDropdowns() {
        val db = RepairShopApp.database
        itemsList = db.getAllItems()
        customersList = db.getAllCustomers()

        val itemNames = itemsList.map { "${it.name} (${it.category})" }
        val itemAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemNames)
        binding.actvEntryItem.setAdapter(itemAdapter)
        if (itemNames.isNotEmpty()) {
            binding.actvEntryItem.setText(itemNames[0], false)
        }

        val customerNames = customersList.map { "${it.name} (${it.phone})" }
        val customerAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, customerNames)
        binding.actvEntryCustomer.setAdapter(customerAdapter)
        if (customerNames.isNotEmpty()) {
            binding.actvEntryCustomer.setText(customerNames[0], false)
        }
    }

    private fun setupDatePicker() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etEntryDate.setText(sdf.format(calendar.time))

        binding.etEntryDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.etEntryDate.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveEntry.setOnClickListener {
            val selectedItemText = binding.actvEntryItem.text?.toString()
            val selectedCustomerText = binding.actvEntryCustomer.text?.toString()
            val dateText = binding.etEntryDate.text?.toString()?.trim() ?: ""
            val remarks = binding.etEntryRemarks.text?.toString()?.trim() ?: ""
            val repairCharge = binding.etRepairCharge.text?.toString()?.toDoubleOrNull() ?: 0.0
            val advancePaid = binding.etAdvancePayment.text?.toString()?.toDoubleOrNull() ?: 0.0

            if (selectedItemText.isNullOrEmpty()) {
                binding.tilEntryItem.error = "Please select an item"
                return@setOnClickListener
            }
            binding.tilEntryItem.error = null

            if (selectedCustomerText.isNullOrEmpty()) {
                binding.tilEntryCustomer.error = "Please select a customer"
                return@setOnClickListener
            }
            binding.tilEntryCustomer.error = null

            val item = itemsList.firstOrNull { "${it.name} (${it.category})" == selectedItemText }
                ?: itemsList.firstOrNull()
            val customer = customersList.firstOrNull { "${it.name} (${it.phone})" == selectedCustomerText }
                ?: customersList.firstOrNull()

            val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = displayDateFormat.format(calendar.time)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = timeFormat.format(calendar.time)

            val entry = TransactionEntry(
                type = if (isEntryIn) "IN" else "OUT",
                itemId = item?.id ?: 0,
                itemName = item?.name ?: selectedItemText,
                itemCategory = item?.category ?: "Electronics",
                itemModel = item?.model ?: "",
                customerId = customer?.id ?: 0,
                customerName = customer?.name ?: selectedCustomerText,
                customerPhone = customer?.phone ?: "",
                dateIn = formattedDate,
                timeIn = formattedTime,
                dateOut = if (!isEntryIn) formattedDate else null,
                repairCharge = if (isEntryIn) repairCharge else 0.0,
                advancePaid = if (isEntryIn) advancePaid else 0.0,
                status = if (isEntryIn) "In Repair" else "Delivered",
                remarks = remarks
            )

            RepairShopApp.database.insertTransaction(entry)
            Toast.makeText(this, "Entry saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
