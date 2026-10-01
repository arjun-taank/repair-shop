package com.repairshop.tracker.ui.items

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.ActivityAddItemBinding
import com.repairshop.tracker.model.Item

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding

    private val categories = arrayOf(
        "Mobile",
        "Laptop",
        "Headphone",
        "Smart Watch",
        "Tablet",
        "Electronics",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoryDropdown()
        setupSaveButton()
    }

    private fun setupToolbar() {
        binding.toolbarAddItem.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvItemCategory.setAdapter(adapter)
        binding.actvItemCategory.setText(categories[0], false)
    }

    private fun setupSaveButton() {
        binding.btnSaveItem.setOnClickListener {
            val name = binding.etItemName.text?.toString()?.trim()
            val category = binding.actvItemCategory.text?.toString()?.trim()
            val brand = binding.etItemBrand.text?.toString()?.trim() ?: ""
            val model = binding.etItemModel.text?.toString()?.trim() ?: ""
            val description = binding.etItemDescription.text?.toString()?.trim() ?: ""

            if (name.isNullOrEmpty()) {
                binding.tilItemName.error = "Please enter an item name"
                return@setOnClickListener
            }
            binding.tilItemName.error = null

            if (category.isNullOrEmpty()) {
                binding.tilItemCategory.error = "Please select a category"
                return@setOnClickListener
            }
            binding.tilItemCategory.error = null

            val newItem = Item(
                name = name,
                category = category,
                brand = brand,
                model = model,
                description = description,
                totalCount = 1,
                inCount = 1,
                outCount = 0
            )

            RepairShopApp.database.insertItem(newItem)
            Toast.makeText(this, "Item saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
