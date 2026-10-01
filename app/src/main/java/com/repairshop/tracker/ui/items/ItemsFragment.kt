package com.repairshop.tracker.ui.items

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.repairshop.tracker.R
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.FragmentItemsBinding
import com.repairshop.tracker.model.Item

class ItemsFragment : Fragment() {

    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ItemsAdapter
    private var allItems: List<Item> = emptyList()
    private var currentFilter: String = "ALL" // "ALL", "IN_STOCK", "OUT_STOCK"
    private var currentSearchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        setupSearch()
        loadItems()

        binding.fabAddItem.setOnClickListener {
            startActivity(Intent(requireContext(), AddItemActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun setupRecyclerView() {
        adapter = ItemsAdapter(emptyList()) { item ->
            // Open AddEntryActivity pre-selected for this item
        }
        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipGroupItemFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chipInStock) -> "IN_STOCK"
                checkedIds.contains(R.id.chipOutStock) -> "OUT_STOCK"
                else -> "ALL"
            }
            applyFilters()
        }
    }

    private fun setupSearch() {
        binding.etSearchItems.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadItems() {
        allItems = RepairShopApp.database.getAllItems()
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allItems

        // Stock status filter
        filtered = when (currentFilter) {
            "IN_STOCK" -> filtered.filter { it.isInStock }
            "OUT_STOCK" -> filtered.filter { !it.isInStock }
            else -> filtered
        }

        // Search query
        if (currentSearchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true) ||
                it.category.contains(currentSearchQuery, ignoreCase = true) ||
                it.brand.contains(currentSearchQuery, ignoreCase = true) ||
                it.model.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ItemsFragment()
    }
}
